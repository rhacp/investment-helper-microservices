import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import {
  Box,
  Card,
  CardContent,
  Chip,
  FormControl,
  InputLabel,
  LinearProgress,
  MenuItem,
  Select,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TableSortLabel,
  TextField,
  Typography,
} from '@mui/material';
import { useQueries, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { useMemo, useState } from 'react';
import { Cell, Line, LineChart, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { portfolioApi } from '../../api/portfolioApi';
import { predictionApi } from '../../api/predictionApi';
import { ChartCard } from '../../components/charts/ChartCard';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import type { PortfolioResponseDTO, PredictionAnalyticsResponseDTO, PredictionResponseDTO } from '../../types/api';
import { formatPercent, normalizeTicker } from '../../utils/formatters';

type PredictionLabelFilter = 'ALL' | 'UP' | 'DOWN';
type ValidationFilter = 'ALL' | 'Pending' | 'Validated';
type SortKey = 'ticker' | 'predictionForDate' | 'confidence';
type SortDirection = 'asc' | 'desc';

interface PredictionRow extends PredictionResponseDTO {
  analytics?: PredictionAnalyticsResponseDTO;
}

const PIE_COLORS = ['#7C3AED', '#22C55E', '#EF4444', '#F59E0B', '#8B5CF6'];

function toPercent(value?: number | null) {
  const numeric = Number(value ?? 0);
  return numeric <= 1 ? numeric * 100 : numeric;
}

function getValidationStatus(prediction: PredictionResponseDTO): 'Pending' | 'Validated' {
  return prediction.correct === true || prediction.correct === false || Boolean(prediction.validatedOn) ? 'Validated' : 'Pending';
}

function getAccuracyResult(prediction: PredictionResponseDTO) {
  if (prediction.correct === true) return 'Correct';
  if (prediction.correct === false) return 'Incorrect';
  return 'Not Yet Validated';
}

export function PredictionsPage() {
  const [search, setSearch] = useState('');
  const [labelFilter, setLabelFilter] = useState<PredictionLabelFilter>('ALL');
  const [validationFilter, setValidationFilter] = useState<ValidationFilter>('ALL');
  const [sortKey, setSortKey] = useState<SortKey>('predictionForDate');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const portfoliosQuery = useQuery({ queryKey: ['portfolios'], queryFn: portfolioApi.getPortfolios });
  const portfolios: PortfolioResponseDTO[] = useMemo(() => portfoliosQuery.data ?? [], [portfoliosQuery.data]);
  const tickerUniverse = useMemo(() => (
    Array.from(
      new Set(
        portfolios
          .flatMap((portfolio) => portfolio.holdings?.map((holding) => holding.ticker) ?? [])
          .map(normalizeTicker)
          .filter(Boolean),
      ),
    ).sort()
  ), [portfolios]);

  const latestQueries = useQueries({
    queries: tickerUniverse.map((ticker) => ({
      queryKey: ['predictionLatest', ticker],
      queryFn: () => predictionApi.latest(ticker),
      retry: false,
      staleTime: 60_000,
    })),
  });

  const analyticsQueries = useQueries({
    queries: tickerUniverse.map((ticker) => ({
      queryKey: ['predictionAnalytics', ticker],
      queryFn: () => predictionApi.analytics(ticker),
      retry: false,
      staleTime: 60_000,
    })),
  });

  const analyticsByTicker = useMemo(() => {
    const entries = analyticsQueries
      .map((query) => query.data)
      .filter(Boolean)
      .map((analytics) => [analytics!.ticker, analytics!] as const);
    return new Map(entries);
  }, [analyticsQueries]);

  const rows: PredictionRow[] = useMemo(() => (
    latestQueries
      .map((query) => query.data)
      .filter(Boolean)
      .map((prediction) => ({ ...prediction!, analytics: analyticsByTicker.get(prediction!.ticker) }))
  ), [analyticsByTicker, latestQueries]);

  const filteredRows = useMemo(() => {
    const normalizedSearch = normalizeTicker(search);
    return rows
      .filter((row) => !normalizedSearch || row.ticker.includes(normalizedSearch))
      .filter((row) => labelFilter === 'ALL' || row.predictionLabel === labelFilter)
      .filter((row) => validationFilter === 'ALL' || getValidationStatus(row) === validationFilter)
      .sort((a, b) => {
        const direction = sortDirection === 'asc' ? 1 : -1;
        if (sortKey === 'confidence') return (toPercent(a.confidence) - toPercent(b.confidence)) * direction;
        if (sortKey === 'predictionForDate') {
          return (dayjs(a.predictionForDate).valueOf() - dayjs(b.predictionForDate).valueOf()) * direction;
        }
        return a.ticker.localeCompare(b.ticker) * direction;
      });
  }, [labelFilter, rows, search, sortDirection, sortKey, validationFilter]);

  const analyticsSummary = useMemo(() => {
    const analytics = Array.from(analyticsByTicker.values());
    const totalPredictions = analytics.reduce((sum, item) => sum + Number(item.totalPredictions ?? 0), 0) || rows.length;
    const validatedPredictions = analytics.reduce((sum, item) => sum + Number(item.validatedPredictions ?? 0), 0);
    const pendingPredictions = analytics.reduce((sum, item) => sum + Number(item.pendingPredictions ?? 0), 0);
    const correctPredictions = analytics.reduce((sum, item) => sum + Number(item.correctPredictions ?? 0), 0);
    const averageConfidence = rows.length
      ? rows.reduce((sum, item) => sum + toPercent(item.confidence), 0) / rows.length
      : 0;
    const accuracy = validatedPredictions ? (correctPredictions / validatedPredictions) * 100 : 0;

    return { totalPredictions, validatedPredictions, pendingPredictions, correctPredictions, accuracy, averageConfidence };
  }, [analyticsByTicker, rows]);

  const paginatedRows = filteredRows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
  const isLoading = portfoliosQuery.isLoading || latestQueries.some((query) => query.isLoading);

  const predictionDistribution = [
    { name: 'UP', value: rows.filter((row) => row.predictionLabel === 'UP').length },
    { name: 'DOWN', value: rows.filter((row) => row.predictionLabel === 'DOWN').length },
  ];
  const validationDistribution = [
    { name: 'Validated', value: analyticsSummary.validatedPredictions || rows.filter((row) => getValidationStatus(row) === 'Validated').length },
    { name: 'Pending', value: analyticsSummary.pendingPredictions || rows.filter((row) => getValidationStatus(row) === 'Pending').length },
  ];
  const correctIncorrectDistribution = [
    { name: 'Correct', value: analyticsSummary.correctPredictions },
    { name: 'Incorrect', value: Math.max(analyticsSummary.validatedPredictions - analyticsSummary.correctPredictions, 0) },
  ];
  const confidenceTrend = [...rows]
    .sort((a, b) => dayjs(a.predictionForDate).valueOf() - dayjs(b.predictionForDate).valueOf())
    .map((row) => ({ label: `${row.ticker} ${dayjs(row.predictionForDate).format('MM-DD')}`, confidence: toPercent(row.confidence) }));
  const accuracyTrend = Array.from(analyticsByTicker.values()).map((item) => ({
    ticker: item.ticker,
    accuracy: toPercent(item.accuracy),
  }));

  const updateSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDirection((current) => (current === 'asc' ? 'desc' : 'asc'));
      return;
    }
    setSortKey(key);
    setSortDirection(key === 'ticker' ? 'asc' : 'desc');
  };

  if (isLoading) return <LoadingSpinner label="Loading predictions" />;
  if (portfoliosQuery.isError) return <ErrorMessage message="Could not load portfolio holdings for predictions." />;

  return (
    <>
      <PageHeader
        eyebrow="Predictions"
        title="Prediction Dashboard"
        subtitle="Review latest stored predictions for tickers already present in your portfolios."
      />

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
        <MetricCard label="Total Predictions" value={analyticsSummary.totalPredictions} description="Stored model predictions available for portfolio tickers." />
        <MetricCard label="Validated" value={analyticsSummary.validatedPredictions} description="Predictions already compared with market movement." />
        <MetricCard label="Pending" value={analyticsSummary.pendingPredictions} description="Predictions waiting for validation." />
        <MetricCard label="Accuracy" value={formatPercent(analyticsSummary.accuracy)} description="Correct predictions divided by validated predictions." />
        <MetricCard label="Avg. Confidence" value={formatPercent(analyticsSummary.averageConfidence)} description="Average confidence across latest signals." />
      </Stack>

      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Stack direction={{ xs: 'column', lg: 'row' }} spacing={2}>
            <TextField
              label="Search by ticker"
              value={search}
              onChange={(event) => {
                setSearch(event.target.value);
                setPage(0);
              }}
              sx={{ minWidth: { lg: 260 } }}
            />
            <FormControl sx={{ minWidth: 220 }}>
              <InputLabel>Prediction</InputLabel>
              <Select label="Prediction" value={labelFilter} onChange={(event) => setLabelFilter(event.target.value as PredictionLabelFilter)}>
                <MenuItem value="ALL">All predictions</MenuItem>
                <MenuItem value="UP">UP</MenuItem>
                <MenuItem value="DOWN">DOWN</MenuItem>
              </Select>
            </FormControl>
            <FormControl sx={{ minWidth: 220 }}>
              <InputLabel>Validation Status</InputLabel>
              <Select label="Validation Status" value={validationFilter} onChange={(event) => setValidationFilter(event.target.value as ValidationFilter)}>
                <MenuItem value="ALL">All statuses</MenuItem>
                <MenuItem value="Pending">Pending</MenuItem>
                <MenuItem value="Validated">Validated</MenuItem>
              </Select>
            </FormControl>
          </Stack>
        </CardContent>
      </Card>

      {!tickerUniverse.length ? (
        <EmptyState title="No portfolio tickers available" message="Add holdings to a portfolio to view stored predictions through public API endpoints." />
      ) : !rows.length ? (
        <EmptyState title="No predictions available" message="Stored predictions will appear here after the backend scheduler creates them for your portfolio tickers." />
      ) : (
        <Stack spacing={3}>
          <Card>
            <CardContent>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>
                        <TableSortLabel active={sortKey === 'ticker'} direction={sortDirection} onClick={() => updateSort('ticker')}>
                          Ticker
                        </TableSortLabel>
                      </TableCell>
                      <TableCell>
                        <TableSortLabel active={sortKey === 'predictionForDate'} direction={sortDirection} onClick={() => updateSort('predictionForDate')}>
                          Prediction Date
                        </TableSortLabel>
                      </TableCell>
                      <TableCell>Prediction</TableCell>
                      <TableCell align="right">
                        <TableSortLabel active={sortKey === 'confidence'} direction={sortDirection} onClick={() => updateSort('confidence')}>
                          Confidence
                        </TableSortLabel>
                      </TableCell>
                      <TableCell align="right">Model Version</TableCell>
                      <TableCell>Validation Status</TableCell>
                      <TableCell>Accuracy Result</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {paginatedRows.map((row) => {
                      const confidence = toPercent(row.confidence);
                      const validationStatus = getValidationStatus(row);
                      const accuracyResult = getAccuracyResult(row);
                      return (
                        <TableRow key={`${row.ticker}-${row.predictionForDate}-${row.modelVersion}`} hover>
                          <TableCell>
                            <Typography fontWeight={800}>{row.ticker}</Typography>
                          </TableCell>
                          <TableCell>{dayjs(row.predictionForDate).format('YYYY-MM-DD')}</TableCell>
                          <TableCell>
                            <Chip
                              size="small"
                              icon={row.predictionLabel === 'UP' ? <ArrowUpwardIcon /> : <ArrowDownwardIcon />}
                              label={row.predictionLabel}
                              color={row.predictionLabel === 'UP' ? 'success' : 'error'}
                              variant="outlined"
                            />
                          </TableCell>
                          <TableCell align="right">
                            <Stack direction="row" spacing={1} alignItems="center" justifyContent="flex-end">
                              <Box sx={{ width: 92 }}>
                                <LinearProgress variant="determinate" value={Math.min(confidence, 100)} />
                              </Box>
                              <Typography fontWeight={800}>{formatPercent(confidence)}</Typography>
                            </Stack>
                          </TableCell>
                          <TableCell align="right">v{row.modelVersion}</TableCell>
                          <TableCell>
                            <Chip size="small" label={validationStatus} color={validationStatus === 'Validated' ? 'success' : 'warning'} variant="outlined" />
                          </TableCell>
                          <TableCell>{accuracyResult}</TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </TableContainer>
              <TablePagination
                component="div"
                count={filteredRows.length}
                page={page}
                rowsPerPage={rowsPerPage}
                rowsPerPageOptions={[5, 10, 20]}
                onPageChange={(_, nextPage) => setPage(nextPage)}
                onRowsPerPageChange={(event) => {
                  setRowsPerPage(Number(event.target.value));
                  setPage(0);
                }}
              />
            </CardContent>
          </Card>

          <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
            <ChartCard title="Accuracy Trend">
              <Box sx={{ height: 260 }}>
                <ResponsiveContainer>
                  <LineChart data={accuracyTrend}>
                    <XAxis dataKey="ticker" stroke="#9CA3AF" />
                    <YAxis stroke="#9CA3AF" domain={[0, 100]} />
                    <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatPercent(Number(value))} />
                    <Line type="monotone" dataKey="accuracy" stroke="#22C55E" strokeWidth={2} dot={{ r: 3 }} />
                  </LineChart>
                </ResponsiveContainer>
              </Box>
            </ChartCard>
            <ChartCard title="Confidence Trend">
              <Box sx={{ height: 260 }}>
                <ResponsiveContainer>
                  <LineChart data={confidenceTrend}>
                    <XAxis dataKey="label" stroke="#9CA3AF" hide />
                    <YAxis stroke="#9CA3AF" domain={[0, 100]} />
                    <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatPercent(Number(value))} />
                    <Line type="monotone" dataKey="confidence" stroke="#8B5CF6" strokeWidth={2} dot={{ r: 3 }} />
                  </LineChart>
                </ResponsiveContainer>
              </Box>
            </ChartCard>
          </Stack>

          <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
            {[
              { title: 'Correct vs Incorrect Predictions', data: correctIncorrectDistribution },
              { title: 'Prediction Distribution', data: predictionDistribution },
              { title: 'Validation Status Distribution', data: validationDistribution },
            ].map((chart) => (
              <ChartCard key={chart.title} title={chart.title}>
                <Box sx={{ height: 240 }}>
                  <ResponsiveContainer>
                    <PieChart>
                      <Pie data={chart.data} dataKey="value" nameKey="name" innerRadius={54} outerRadius={84}>
                        {chart.data.map((entry, index) => (
                          <Cell key={entry.name} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} />
                    </PieChart>
                  </ResponsiveContainer>
                </Box>
              </ChartCard>
            ))}
          </Stack>
        </Stack>
      )}
    </>
  );
}
