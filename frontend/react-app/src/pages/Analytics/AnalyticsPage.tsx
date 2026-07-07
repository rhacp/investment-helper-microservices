import SearchIcon from '@mui/icons-material/Search';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Box,
  Button,
  Card,
  CardContent,
  LinearProgress,
  MenuItem,
  Stack,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tabs,
  TextField,
  Typography,
} from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { z } from 'zod';
import { analyticsApi } from '../../api/analyticsApi';
import { marketDataApi } from '../../api/marketDataApi';
import { portfolioApi } from '../../api/portfolioApi';
import { AllocationPieChart } from '../../components/charts/AllocationPieChart';
import { ChartCard } from '../../components/charts/ChartCard';
import { HoldingsBarChart } from '../../components/charts/HoldingsBarChart';
import { PriceLineChart } from '../../components/charts/PriceLineChart';
import { RiskBarChart } from '../../components/charts/RiskBarChart';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import type { MarketPriceResponseDTO, PortfolioHoldingAnalyticsResponseDTO, PortfolioResponseDTO } from '../../types/api';
import { formatCurrency, formatNumber, formatPercent, formatRatioPercent, getSignedColor, normalizeTicker } from '../../utils/formatters';

const schema = z.object({ ticker: z.string().min(1, 'Ticker is required') });
type AnalyticsSearchValues = z.infer<typeof schema>;

function buildReturnSeries(prices: MarketPriceResponseDTO[]) {
  return prices.slice(1).map((price, index) => {
    const previousClose = Number(prices[index].closePrice || 0);
    const currentClose = Number(price.closePrice || 0);
    const dailyReturn = previousClose ? ((currentClose - previousClose) / previousClose) * 100 : 0;
    return {
      date: price.priceDate ?? price.date ?? `${index + 1}`,
      dailyReturn,
    };
  });
}

export function AnalyticsPage() {
  const [activeTab, setActiveTab] = useState(0);
  const [ticker, setTicker] = useState('');
  const [selectedPortfolioId, setSelectedPortfolioId] = useState<number | ''>('');
  const form = useForm<AnalyticsSearchValues>({
    resolver: zodResolver(schema),
    defaultValues: { ticker: '' },
  });

  const portfoliosQuery = useQuery({ queryKey: ['portfolios'], queryFn: portfolioApi.getPortfolios });
  const portfolios: PortfolioResponseDTO[] = portfoliosQuery.data ?? [];
  const activePortfolioId = Number(selectedPortfolioId || portfolios[0]?.id || 0);

  const stockAnalyticsQuery = useQuery({
    queryKey: ['stockAnalytics', ticker],
    queryFn: () => analyticsApi.getStockAnalytics(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });
  const stockQuery = useQuery({
    queryKey: ['stock', ticker],
    queryFn: () => marketDataApi.getStock(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });
  const stockHistoryQuery = useQuery({
    queryKey: ['stockHistory', ticker],
    queryFn: () => marketDataApi.getHistory(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });
  const portfolioAnalyticsQuery = useQuery({
    queryKey: ['portfolioAnalytics', activePortfolioId],
    queryFn: () => analyticsApi.getPortfolioAnalytics(activePortfolioId),
    enabled: Boolean(activePortfolioId),
    retry: false,
  });

  if (portfoliosQuery.isLoading) return <LoadingSpinner label="Loading analytics" />;

  const stockAnalytics = stockAnalyticsQuery.data;
  const portfolioAnalytics = portfolioAnalyticsQuery.data;
  const stockPrices = stockHistoryQuery.data ?? [];
  const returnSeries = buildReturnSeries(stockPrices).slice(-30);
  const bestWorstDayData = [
    { name: 'Best Day', value: Number(stockAnalytics?.bestDayReturn ?? 0) },
    { name: 'Worst Day', value: Number(stockAnalytics?.worstDayReturn ?? 0) },
  ];
  const volatilityValue = Math.min(Math.abs(Number(stockAnalytics?.annualizedVolatility ?? 0)), 100);
  const holdings: PortfolioHoldingAnalyticsResponseDTO[] = portfolioAnalytics?.holdings ?? [];

  return (
    <>
      <PageHeader
        title="Investment Analytics"
      />

      <Card sx={{ mb: 3 }}>
        <Tabs value={activeTab} onChange={(_, value) => setActiveTab(value)} variant="scrollable" scrollButtons="auto">
          <Tab label="Stock Analytics" />
          <Tab label="Portfolio Analytics" />
        </Tabs>
      </Card>

      {activeTab === 0 && (
        <Stack spacing={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Stock Analytics
              </Typography>
              <Stack
                component="form"
                direction={{ xs: 'column', sm: 'row' }}
                spacing={2}
                onSubmit={form.handleSubmit((values) => setTicker(normalizeTicker(values.ticker)))}
              >
                <TextField
                  label="Ticker"
                  placeholder="AAPL"
                  {...form.register('ticker')}
                  error={Boolean(form.formState.errors.ticker)}
                  helperText={form.formState.errors.ticker?.message}
                  sx={{ minWidth: { sm: 280 } }}
                />
                <Button type="submit" variant="contained" startIcon={<SearchIcon />}>
                  Load Analytics
                </Button>
              </Stack>
            </CardContent>
          </Card>
          {!ticker ? (
            <Typography color="text.secondary">Enter a ticker to load stock analytics.</Typography>
          ) : stockQuery.isLoading || stockAnalyticsQuery.isLoading ? (
            <LoadingSpinner label="Loading stock analytics" />
          ) : stockQuery.isError ? (
            <ErrorMessage
              title="Stock not available"
              message={
                'This stock has not been imported yet.\n\nPlease verify the ticker symbol or add the stock to one of your portfolios. Investment Helper will automatically synchronize market data and generate analytics and predictions.'
              }
            />
          ) : stockAnalyticsQuery.isError ? (
            <ErrorMessage
              title="Analytics unavailable"
              message="This stock exists, but analytics are not available for it yet."
            />
          ) : stockAnalytics ? (
            <>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                <MetricCard label="Total Return" value={formatRatioPercent(stockAnalytics.totalReturn)} color={getSignedColor(stockAnalytics.totalReturn)} />
                <MetricCard label="Average Daily Return" value={formatRatioPercent(stockAnalytics.averageDailyReturn)} color={getSignedColor(stockAnalytics.averageDailyReturn)} />
                <MetricCard label="Volatility" value={formatNumber(stockAnalytics.dailyVolatility)} />
                <MetricCard label="Annualized Volatility" value={formatNumber(stockAnalytics.annualizedVolatility)} />
              </Stack>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                <MetricCard label="Sharpe Ratio" value={formatNumber(stockAnalytics.sharpeRatio)} />
                <MetricCard label="Max Drawdown" value={formatRatioPercent(stockAnalytics.maxDrawdown)} color={getSignedColor(stockAnalytics.maxDrawdown)} />
                <MetricCard label="Best Day Return" value={formatRatioPercent(stockAnalytics.bestDayReturn)} color="success.main" />
                <MetricCard label="Worst Day Return" value={formatRatioPercent(stockAnalytics.worstDayReturn)} color="error.main" />
              </Stack>
              <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
                <ChartCard title="Historical Price Chart">
                  <PriceLineChart prices={stockPrices} />
                </ChartCard>
                <ChartCard title="Risk Metrics Bar Chart">
                  <RiskBarChart data={stockAnalytics} />
                </ChartCard>
              </Stack>
              <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
                <ChartCard title="Return Distribution Chart">
                  <Box sx={{ height: 280 }}>
                    <ResponsiveContainer>
                      <BarChart data={returnSeries}>
                        <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
                        <XAxis dataKey="date" stroke="#9CA3AF" hide />
                        <YAxis stroke="#9CA3AF" />
                        <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatPercent(Number(value))} />
                        <Bar dataKey="dailyReturn" fill="#8B5CF6" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </Box>
                </ChartCard>
                <ChartCard title="Best vs Worst Day Comparison">
                  <Box sx={{ height: 280 }}>
                    <ResponsiveContainer>
                      <BarChart data={bestWorstDayData}>
                        <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
                        <XAxis dataKey="name" stroke="#9CA3AF" />
                        <YAxis stroke="#9CA3AF" />
                        <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatRatioPercent(Number(value))} />
                        <Bar dataKey="value" fill="#22C55E" radius={[4, 4, 0, 0]} />
                      </BarChart>
                    </ResponsiveContainer>
                  </Box>
                </ChartCard>
                <Card sx={{ flex: 1, minWidth: 0 }}>
                  <CardContent>
                    <Typography variant="h6" sx={{ mb: 2 }}>
                      Volatility Indicator
                    </Typography>
                    <Typography variant="h4" sx={{ mb: 1 }}>
                      {formatNumber(stockAnalytics.annualizedVolatility)}
                    </Typography>
                    <LinearProgress variant="determinate" value={volatilityValue} sx={{ mb: 2 }} />
                    <Typography color="text.secondary">
                      Annualized volatility gives a quick read on the stock's risk profile.
                    </Typography>
                  </CardContent>
                </Card>
              </Stack>
            </>
          ) : null}
        </Stack>
      )}

      {activeTab === 1 && (
        <Stack spacing={3}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                Portfolio Analytics
              </Typography>
              <TextField
                select
                label="Select Portfolio"
                value={activePortfolioId || ''}
                onChange={(event) => setSelectedPortfolioId(Number(event.target.value))}
                sx={{ minWidth: { xs: '100%', sm: 320 } }}
              >
                {portfolios.map((portfolio) => (
                  <MenuItem key={portfolio.id} value={portfolio.id}>
                    {portfolio.name}
                  </MenuItem>
                ))}
              </TextField>
            </CardContent>
          </Card>
          {!portfolios.length ? (
            <EmptyState title="No portfolios available" message="Create a portfolio and add holdings to view portfolio analytics." />
          ) : portfolioAnalyticsQuery.isLoading ? (
            <LoadingSpinner label="Loading portfolio analytics" />
          ) : portfolioAnalyticsQuery.isError ? (
            <ErrorMessage message="Portfolio analytics are not available yet. Add holdings or try another portfolio." />
          ) : portfolioAnalytics ? (
            <>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
                <MetricCard label="Current Portfolio Value" value={formatCurrency(portfolioAnalytics.currentValue)} />
                <MetricCard label="Total Profit/Loss" value={formatCurrency(portfolioAnalytics.totalProfitLoss)} color={getSignedColor(portfolioAnalytics.totalProfitLoss)} />
                <MetricCard label="Total Return" value={formatRatioPercent(portfolioAnalytics.totalReturn)} color={getSignedColor(portfolioAnalytics.totalReturn)} />
                <MetricCard label="Number of Holdings" value={portfolioAnalytics.numberOfHoldings} />
              </Stack>
              <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
                <ChartCard title="Portfolio Allocation Pie Chart">
                  <AllocationPieChart holdings={holdings} />
                </ChartCard>
                <ChartCard title="Holdings Profit/Loss Bar Chart">
                  <HoldingsBarChart holdings={holdings} metric="profitLoss" />
                </ChartCard>
                <ChartCard title="Portfolio Risk Dashboard">
                  <RiskBarChart data={portfolioAnalytics.risk} />
                </ChartCard>
              </Stack>
              <Card>
                <CardContent>
                  <Typography variant="h6" sx={{ mb: 2 }}>
                    Holdings Performance Table
                  </Typography>
                  <TableContainer>
                    <Table size="small">
                      <TableHead>
                        <TableRow>
                          <TableCell>Ticker</TableCell>
                          <TableCell align="right">Quantity</TableCell>
                          <TableCell align="right">Average Buy</TableCell>
                          <TableCell align="right">Current Price</TableCell>
                          <TableCell align="right">Current Value</TableCell>
                          <TableCell align="right">Profit/Loss</TableCell>
                          <TableCell align="right">Return</TableCell>
                          <TableCell align="right">Weight</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {holdings.map((holding) => (
                          <TableRow key={holding.ticker} hover>
                            <TableCell>
                              <Typography fontWeight={800}>{holding.ticker}</Typography>
                            </TableCell>
                            <TableCell align="right">{formatNumber(holding.quantity)}</TableCell>
                            <TableCell align="right">{formatCurrency(holding.averageBuyPrice)}</TableCell>
                            <TableCell align="right">{formatCurrency(holding.currentPrice)}</TableCell>
                            <TableCell align="right">{formatCurrency(holding.currentValue)}</TableCell>
                            <TableCell align="right" sx={{ color: getSignedColor(holding.profitLoss), fontWeight: 800 }}>
                              {formatCurrency(holding.profitLoss)}
                            </TableCell>
                            <TableCell align="right" sx={{ color: getSignedColor(holding.returnPercentage), fontWeight: 800 }}>
                              {formatRatioPercent(holding.returnPercentage)}
                            </TableCell>
                            <TableCell align="right">{formatRatioPercent(holding.weight)}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                </CardContent>
              </Card>
            </>
          ) : null}
        </Stack>
      )}
    </>
  );
}
