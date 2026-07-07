import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import {
  Box,
  Card,
  CardContent,
  Chip,
  FormControl,
  IconButton,
  InputAdornment,
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
import ClearIcon from '@mui/icons-material/Clear';
import { useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { useMemo, useState } from 'react';
import { Cell, Line, LineChart, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { predictionApi } from '../../api/predictionApi';
import { ChartCard } from '../../components/charts/ChartCard';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import type { PredictionResponseDTO } from '../../types/api';
import { formatPercent, normalizeTicker } from '../../utils/formatters';

type PredictionLabelFilter = 'ALL' | 'UP' | 'DOWN';
type ValidationFilter = 'ALL' | 'Pending' | 'Validated';
type SortKey = 'ticker' | 'predictionForDate' | 'confidence';
type SortDirection = 'asc' | 'desc';

type ValidationStatus = 'Pending' | 'Validated';
type AccuracyResult = 'Correct' | 'Incorrect' | 'Pending / Not validated';

interface PredictionRow extends PredictionResponseDTO {
  id: string;
}

const PIE_COLORS = ['#7C3AED', '#22C55E', '#EF4444', '#F59E0B', '#8B5CF6'];

function toPercent(value?: number | null) {
  const numeric = Number(value ?? 0);
  return numeric <= 1 ? numeric * 100 : numeric;
}

function getValidationStatus(prediction: PredictionResponseDTO): ValidationStatus {
  return prediction.correct === null || prediction.correct === undefined ? 'Pending' : 'Validated';
}

function getAccuracyResult(prediction: PredictionResponseDTO): AccuracyResult {
  if (prediction.correct === true) return 'Correct';
  if (prediction.correct === false) return 'Incorrect';
  return 'Pending / Not validated';
}

function getPredictionId(prediction: PredictionResponseDTO, index: number) {
  return [
    prediction.ticker,
    prediction.predictionForDate,
    prediction.modelVersion,
    prediction.createdAt ?? index,
  ].join('-');
}

export function PredictionsPage() {
  const [search, setSearch] = useState('');
  const [labelFilter, setLabelFilter] = useState<PredictionLabelFilter>('ALL');
  const [validationFilter, setValidationFilter] = useState<ValidationFilter>('ALL');
  const [predictionDateFilter, setPredictionDateFilter] = useState('');
  const [sortKey, setSortKey] = useState<SortKey>('predictionForDate');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const normalizedSearch = normalizeTicker(search);

  const allHistoryQuery = useQuery({
    queryKey: ['predictionHistory', 'all'],
    queryFn: () => predictionApi.history(''),
    retry: false,
    staleTime: 60_000,
  });

  const availableTickers = useMemo(() => {
    const history = (allHistoryQuery.data ?? []) as PredictionResponseDTO[];
    return Array.from(new Set(history.map((prediction: PredictionResponseDTO) => normalizeTicker(prediction.ticker))));
  }, [allHistoryQuery.data]);

  const shouldFetchTickerHistory = Boolean(normalizedSearch) && availableTickers.includes(normalizedSearch);

  const tickerHistoryQuery = useQuery({
    queryKey: ['predictionHistory', normalizedSearch],
    queryFn: () => predictionApi.history(normalizedSearch),
    enabled: shouldFetchTickerHistory,
    retry: false,
    staleTime: 60_000,
  });

  const rows: PredictionRow[] = useMemo(() => {
    const seen = new Set<string>();
    const history = (
      normalizedSearch && shouldFetchTickerHistory
        ? (tickerHistoryQuery.data ?? [])
        : (allHistoryQuery.data ?? [])
    ) as PredictionResponseDTO[];

    return history
      .filter((prediction: PredictionResponseDTO, index: number) => {
        const dedupeKey = getPredictionId(prediction, index);
        if (seen.has(dedupeKey)) {
          return false;
        }
        seen.add(dedupeKey);
        return true;
      })
      .map((prediction: PredictionResponseDTO, index: number) => ({
        ...prediction,
        id: getPredictionId(prediction, index),
      }))
      .sort((a: PredictionRow, b: PredictionRow) => dayjs(b.predictionForDate).valueOf() - dayjs(a.predictionForDate).valueOf());
  }, [allHistoryQuery.data, normalizedSearch, shouldFetchTickerHistory, tickerHistoryQuery.data]);

  const filteredRows = useMemo(() => {
    return [...rows]
      .filter((row) => !normalizedSearch || row.ticker.includes(normalizedSearch))
      .filter((row) => labelFilter === 'ALL' || row.predictionLabel === labelFilter)
      .filter((row) => validationFilter === 'ALL' || getValidationStatus(row) === validationFilter)
      .filter((row) => !predictionDateFilter || dayjs(row.predictionForDate).format('YYYY-MM-DD') === predictionDateFilter)
      .sort((a, b) => {
        const direction = sortDirection === 'asc' ? 1 : -1;

        if (sortKey === 'confidence') {
          return (toPercent(a.confidence) - toPercent(b.confidence)) * direction;
        }

        if (sortKey === 'predictionForDate') {
          const dateComparison = (dayjs(a.predictionForDate).valueOf() - dayjs(b.predictionForDate).valueOf()) * direction;
          if (dateComparison !== 0) {
            return dateComparison;
          }
          const createdAtComparison = ((dayjs(a.createdAt).valueOf() || 0) - (dayjs(b.createdAt).valueOf() || 0)) * direction;
          if (createdAtComparison !== 0) {
            return createdAtComparison;
          }
          return (a.modelVersion - b.modelVersion) * direction;
        }

        return a.ticker.localeCompare(b.ticker) * direction;
      });
  }, [labelFilter, normalizedSearch, predictionDateFilter, rows, sortDirection, sortKey, validationFilter]);

  const validatedRows = useMemo(
    () => filteredRows.filter((row) => row.correct !== null && row.correct !== undefined),
    [filteredRows],
  );

  const analyticsSummary = useMemo(() => {
    const totalPredictions = filteredRows.length;
    const validatedPredictions = validatedRows.length;
    const pendingPredictions = totalPredictions - validatedPredictions;
    const correctPredictions = validatedRows.filter((row) => row.correct === true).length;
    const averageConfidence = totalPredictions
      ? filteredRows.reduce((sum, row) => sum + toPercent(row.confidence), 0) / totalPredictions
      : 0;
    const accuracy = validatedPredictions ? (correctPredictions / validatedPredictions) * 100 : 0;

    return { totalPredictions, validatedPredictions, pendingPredictions, correctPredictions, averageConfidence, accuracy };
  }, [filteredRows, validatedRows]);

  const paginatedRows = filteredRows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);
  const isLoading = allHistoryQuery.isLoading || tickerHistoryQuery.isLoading;
  const hasHistoryError = allHistoryQuery.isError || tickerHistoryQuery.isError;

  const predictionDistribution = useMemo(() => ([
    { name: 'UP', value: filteredRows.filter((row) => row.predictionLabel === 'UP').length },
    { name: 'DOWN', value: filteredRows.filter((row) => row.predictionLabel === 'DOWN').length },
  ]), [filteredRows]);

  const validationStatusDistribution = useMemo(() => ([
    { name: 'Correct', value: analyticsSummary.correctPredictions },
    { name: 'Incorrect', value: Math.max(analyticsSummary.validatedPredictions - analyticsSummary.correctPredictions, 0) },
    { name: 'Pending', value: analyticsSummary.pendingPredictions },
  ]), [analyticsSummary.correctPredictions, analyticsSummary.pendingPredictions, analyticsSummary.validatedPredictions]);

  const correctIncorrectDistribution = useMemo(() => ([
    { name: 'Correct', value: analyticsSummary.correctPredictions },
    { name: 'Incorrect', value: Math.max(analyticsSummary.validatedPredictions - analyticsSummary.correctPredictions, 0) },
  ]), [analyticsSummary.correctPredictions, analyticsSummary.validatedPredictions]);

  const confidenceTrend = useMemo(() => (
    [...filteredRows]
      .sort((a, b) => dayjs(a.predictionForDate).valueOf() - dayjs(b.predictionForDate).valueOf())
      .map((row, index) => ({
        id: `${row.id}-${index}`,
        date: dayjs(row.predictionForDate).format('YYYY-MM-DD'),
        label: `${row.ticker} ${dayjs(row.predictionForDate).format('MM-DD')}`,
        confidence: toPercent(row.confidence),
      }))
  ), [filteredRows]);

  const validationResults = useMemo(() => (
    Array.from(
      validatedRows.reduce((map, row) => {
        const current = map.get(row.ticker) ?? { ticker: row.ticker, correct: 0, total: 0 };
        current.total += 1;
        if (row.correct === true) {
          current.correct += 1;
        }
        map.set(row.ticker, current);
        return map;
      }, new Map<string, { ticker: string; correct: number; total: number }>()),
    )
      .map(([, item]) => ({
        ticker: item.ticker,
        accuracy: item.total ? (item.correct / item.total) * 100 : 0,
      }))
      .sort((a, b) => a.ticker.localeCompare(b.ticker))
  ), [validatedRows]);

  const modelAccuracy = useMemo(() => ({
    correctPredictions: analyticsSummary.correctPredictions,
    totalValidated: analyticsSummary.validatedPredictions,
    percentage: analyticsSummary.accuracy,
  }), [analyticsSummary.accuracy, analyticsSummary.correctPredictions, analyticsSummary.validatedPredictions]);

  const updateSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDirection((current) => (current === 'asc' ? 'desc' : 'asc'));
      return;
    }
    setSortKey(key);
    setSortDirection(key === 'ticker' ? 'asc' : 'desc');
  };

  if (isLoading) return <LoadingSpinner label="Loading predictions" />;
  if (hasHistoryError) return <ErrorMessage message="Could not load prediction history." />;

  return (
    <>
      <PageHeader title="Prediction Dashboard" />

      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
        <MetricCard label="Total Predictions" value={analyticsSummary.totalPredictions} description="Stored model predictions available for portfolio tickers." />
        <MetricCard label="Validated" value={analyticsSummary.validatedPredictions} description="Predictions already compared with market movement." />
        <MetricCard label="Pending" value={analyticsSummary.pendingPredictions} description="Predictions waiting for validation." />
        <MetricCard label="Accuracy" value={formatPercent(analyticsSummary.accuracy)} description="Correct predictions divided by validated predictions." />
        <MetricCard label="Avg. Confidence" value={formatPercent(analyticsSummary.averageConfidence)} description="Average confidence across prediction history." />
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
              <Select
                label="Prediction"
                value={labelFilter}
                onChange={(event) => {
                  setLabelFilter(event.target.value as PredictionLabelFilter);
                  setPage(0);
                }}
              >
                <MenuItem value="ALL">All predictions</MenuItem>
                <MenuItem value="UP">UP</MenuItem>
                <MenuItem value="DOWN">DOWN</MenuItem>
              </Select>
            </FormControl>
            <FormControl sx={{ minWidth: 220 }}>
              <InputLabel>Validation Status</InputLabel>
              <Select
                label="Validation Status"
                value={validationFilter}
                onChange={(event) => {
                  setValidationFilter(event.target.value as ValidationFilter);
                  setPage(0);
                }}
              >
                <MenuItem value="ALL">All statuses</MenuItem>
                <MenuItem value="Pending">Pending</MenuItem>
                <MenuItem value="Validated">Validated</MenuItem>
              </Select>
            </FormControl>
            <TextField
              label="Prediction Date"
              type="date"
              value={predictionDateFilter}
              onChange={(event) => {
                setPredictionDateFilter(event.target.value);
                setPage(0);
              }}
              InputLabelProps={{ shrink: true }}
              InputProps={{
                endAdornment: predictionDateFilter ? (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="Clear prediction date"
                      edge="end"
                      onClick={() => {
                        setPredictionDateFilter('');
                        setPage(0);
                      }}
                      size="small"
                    >
                      <ClearIcon fontSize="small" />
                    </IconButton>
                  </InputAdornment>
                ) : undefined,
              }}
              sx={{ minWidth: 220 }}
            />
          </Stack>
        </CardContent>
      </Card>

      {!rows.length ? (
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
                        <TableRow key={row.id} hover>
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
                rowsPerPageOptions={[5, 10]}
                onPageChange={(_, nextPage) => setPage(nextPage)}
                onRowsPerPageChange={(event) => {
                  setRowsPerPage(Number(event.target.value));
                  setPage(0);
                }}
              />
            </CardContent>
          </Card>

          <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3} sx={{ alignItems: 'stretch' }}>
            <ChartCard title="Validation Results">
              {!validationResults.length ? (
                <EmptyState title="No validated predictions available" message="Accuracy will appear after predictions are validated by the backend." />
              ) : (
                <Box sx={{ height: 260 }}>
                  <ResponsiveContainer>
                    <LineChart data={validationResults}>
                      <XAxis dataKey="ticker" stroke="#9CA3AF" />
                      <YAxis stroke="#9CA3AF" domain={[0, 100]} />
                      <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatPercent(Number(value))} />
                      <Line type="monotone" dataKey="accuracy" stroke="#22C55E" strokeWidth={2} dot={{ r: 3 }} />
                    </LineChart>
                  </ResponsiveContainer>
                </Box>
              )}
            </ChartCard>
            <ChartCard title="Model Accuracy">
              <Box sx={{ height: 260, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
                {!modelAccuracy.totalValidated ? (
                  <EmptyState title="No validated predictions available" message="Model accuracy will appear after predictions are validated by the backend." />
                ) : (
                  <Stack spacing={2}>
                    <Typography variant="h3" fontWeight={900}>
                      {formatPercent(modelAccuracy.percentage)}
                    </Typography>
                    <LinearProgress variant="determinate" value={Math.min(modelAccuracy.percentage, 100)} />
                    <Typography color="text.secondary">
                      {modelAccuracy.correctPredictions} correct out of {modelAccuracy.totalValidated} validated predictions
                    </Typography>
                  </Stack>
                )}
              </Box>
            </ChartCard>
          </Stack>

          <Box sx={{ width: '100%', display: 'flex', justifyContent: 'center' }}>
            <ChartCard title="Confidence Trend">
              {!confidenceTrend.length ? (
                <EmptyState title="No prediction history available" message="Confidence trend will appear once predictions exist for your portfolio tickers." />
              ) : (
                <Box sx={{ height: 320 }}>
                  <ResponsiveContainer>
                    <LineChart data={confidenceTrend}>
                      <XAxis dataKey="date" stroke="#9CA3AF" />
                      <YAxis stroke="#9CA3AF" domain={[0, 100]} />
                      <Tooltip contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }} formatter={(value) => formatPercent(Number(value))} />
                      <Line type="monotone" dataKey="confidence" stroke="#8B5CF6" strokeWidth={2} dot={{ r: 3 }} />
                    </LineChart>
                  </ResponsiveContainer>
                </Box>
              )}
            </ChartCard>
          </Box>

          <Stack direction={{ xs: 'column', xl: 'row' }} spacing={3}>
            {[
              { title: 'Correct vs Incorrect Predictions', data: correctIncorrectDistribution },
              { title: 'Prediction Distribution', data: predictionDistribution },
              { title: 'Validation Status Distribution', data: validationStatusDistribution },
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
