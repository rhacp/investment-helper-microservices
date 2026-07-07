import SearchIcon from '@mui/icons-material/Search';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Card, CardContent, Chip, Stack, TextField, Typography } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { analyticsApi } from '../../api/analyticsApi';
import { marketDataApi } from '../../api/marketDataApi';
import { ChartCard } from '../../components/charts/ChartCard';
import { PriceLineChart } from '../../components/charts/PriceLineChart';
import { RiskBarChart } from '../../components/charts/RiskBarChart';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import { formatCurrency, formatNumber, formatRatioPercent, normalizeTicker } from '../../utils/formatters';

const schema = z.object({ ticker: z.string().min(1, 'Ticker is required') });
type StockSearchValues = z.infer<typeof schema>;

export function StocksPage() {
  const [ticker, setTicker] = useState('');
  const form = useForm<StockSearchValues>({
    resolver: zodResolver(schema),
    defaultValues: { ticker: '' },
  });

  const stockQuery = useQuery({
    queryKey: ['stock', ticker],
    queryFn: () => marketDataApi.getStock(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });
  const historyQuery = useQuery({
    queryKey: ['stockHistory', ticker],
    queryFn: () => marketDataApi.getHistory(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });
  const analyticsQuery = useQuery({
    queryKey: ['stockAnalytics', ticker],
    queryFn: () => analyticsApi.getStockAnalytics(ticker),
    enabled: Boolean(ticker),
    retry: false,
  });

  const analytics = analyticsQuery.data;

  return (
    <>
      <PageHeader
        title="Market Data"
      />
      <Card sx={{ mb: 3 }}>
        <CardContent>
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
              Load Market Data
            </Button>
          </Stack>
        </CardContent>
      </Card>

      {!ticker ? (
        <Typography color="text.secondary">Enter a ticker to load market data.</Typography>
      ) : stockQuery.isLoading ? (
        <LoadingSpinner label="Loading stock" />
      ) : stockQuery.isError ? (
        <ErrorMessage
          title="Stock not available"
          message={
            'This stock has not been imported yet.\n\nPlease verify the ticker symbol or add the stock to one of your portfolios. Investment Helper will automatically synchronize market data and generate analytics and predictions.'
          }
        />
      ) : stockQuery.data ? (
        <Stack spacing={3}>
          <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ alignItems: 'stretch' }}>
            <MetricCard label="Ticker" value={<Chip label={stockQuery.data.ticker} color="primary" />} helper={stockQuery.data.name} />
            <MetricCard label="Exchange" value={stockQuery.data.exchange ?? 'N/A'} helper={stockQuery.data.currency} />
            <MetricCard label="Current price" value={formatCurrency(analytics?.currentPrice)} />
            <MetricCard label="Data points" value={analytics?.dataPoints ?? historyQuery.data?.length ?? 0} />
          </Stack>
          <ChartCard title="Historical stock price">
            <PriceLineChart prices={historyQuery.data ?? []} />
          </ChartCard>
          <Stack direction={{ xs: 'column', lg: 'row' }} spacing={3}>
            <ChartCard title="Risk comparison">
              <RiskBarChart data={analytics} />
            </ChartCard>
            <Card sx={{ flex: 1 }}>
              <CardContent>
                <Typography variant="h6" sx={{ mb: 2 }}>
                  Stock analytics
                </Typography>
                <Stack spacing={1.5}>
                  <Typography>Total return: {formatRatioPercent(analytics?.totalReturn)}</Typography>
                  <Typography>Average daily return: {formatRatioPercent(analytics?.averageDailyReturn)}</Typography>
                  <Typography>Annualized volatility: {formatNumber(analytics?.annualizedVolatility)}</Typography>
                  <Typography>Sharpe ratio: {formatNumber(analytics?.sharpeRatio)}</Typography>
                  <Typography>Max drawdown: {formatRatioPercent(analytics?.maxDrawdown)}</Typography>
                </Stack>
              </CardContent>
            </Card>
          </Stack>
        </Stack>
      ) : null}
    </>
  );
}
