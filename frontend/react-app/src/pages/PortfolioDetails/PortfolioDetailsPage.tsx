import AddIcon from '@mui/icons-material/Add';
import AnalyticsOutlinedIcon from '@mui/icons-material/AnalyticsOutlined';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { Button, Card, CardContent, Stack } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { AllocationPieChart } from '../../components/charts/AllocationPieChart';
import { ChartCard } from '../../components/charts/ChartCard';
import { HoldingsBarChart } from '../../components/charts/HoldingsBarChart';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import { HoldingDialog, type HoldingFormValues } from '../../components/portfolio/HoldingDialog';
import { HoldingsTable } from '../../components/portfolio/HoldingsTable';
import { analyticsService } from '../../services/analyticsService';
import { portfolioService } from '../../services/portfolioService';
import type { HoldingResponseDTO, PortfolioAnalyticsResponseDTO, PortfolioResponseDTO } from '../../types/api';
import { formatCurrency, formatRatioPercent, getSignedColor } from '../../utils/formatters';

export function PortfolioDetailsPage() {
  const { portfolioId } = useParams();
  const id = Number(portfolioId);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingHolding, setEditingHolding] = useState<HoldingResponseDTO | null>(null);
  const [deletingHolding, setDeletingHolding] = useState<HoldingResponseDTO | null>(null);
  const [holdingFormError, setHoldingFormError] = useState('');

  const portfolioQuery = useQuery({
    queryKey: ['portfolio', id],
    queryFn: () => portfolioService.getPortfolio(id),
    enabled: Number.isFinite(id),
  });
  const analyticsQuery = useQuery({
    queryKey: ['portfolioAnalytics', id],
    queryFn: () => analyticsService.getPortfolioAnalytics(id),
    enabled: Number.isFinite(id),
    retry: false,
  });

  const invalidate = () => {
    queryClient.invalidateQueries({ queryKey: ['portfolio', id] });
    queryClient.invalidateQueries({ queryKey: ['portfolios'] });
    queryClient.invalidateQueries({ queryKey: ['portfolioAnalytics', id] });
  };

  const createHoldingMutation = useMutation({
    mutationFn: (values: HoldingFormValues) => portfolioService.createHolding(id, values),
    onSuccess: () => {
      toast.success('Holding added');
      setDialogOpen(false);
      setHoldingFormError('');
      invalidate();
    },
    onError: () => {
      setHoldingFormError('Please verify the ticker symbol. Investment Helper could not add this holding because market data is not available for the entered ticker.');
    },
  });

  const updateHoldingMutation = useMutation({
    mutationFn: ({ holdingId, values }: { holdingId: number; values: HoldingFormValues }) =>
      portfolioService.updateHolding(holdingId, {
        quantity: values.quantity,
        averageBuyPrice: values.averageBuyPrice,
        purchaseDate: values.purchaseDate,
      }),
    onSuccess: () => {
      toast.success('Holding updated');
      setDialogOpen(false);
      setEditingHolding(null);
      setHoldingFormError('');
      invalidate();
    },
    onError: () => {
      setHoldingFormError('Please check the holding values and try saving again.');
    },
  });

  const deleteHoldingMutation = useMutation({
    mutationFn: portfolioService.deleteHolding,
    onSuccess: () => {
      toast.success('Holding deleted');
      setDeletingHolding(null);
      invalidate();
    },
  });

  if (portfolioQuery.isLoading) return <LoadingSpinner label="Loading portfolio" />;
  if (portfolioQuery.isError || !portfolioQuery.data) return <ErrorMessage message="Could not load this portfolio." />;

  const portfolio = portfolioQuery.data as PortfolioResponseDTO;
  const holdings: HoldingResponseDTO[] = portfolio.holdings ?? [];
  const analytics = analyticsQuery.data as PortfolioAnalyticsResponseDTO | undefined;
  const totalInvested = analytics?.totalInvested ?? holdings.reduce((sum: number, holding: HoldingResponseDTO) => sum + holding.quantity * holding.averageBuyPrice, 0);
  const totalReturn = analytics?.totalReturn ?? (totalInvested ? portfolio.totalProfitLoss / totalInvested : 0);
  const isSaving = createHoldingMutation.isPending || updateHoldingMutation.isPending;

  const submitHolding = (values: HoldingFormValues) => {
    setHoldingFormError('');
    if (editingHolding) {
      updateHoldingMutation.mutate({ holdingId: editingHolding.id, values });
      return;
    }
    createHoldingMutation.mutate(values);
  };

  return (
    <>
      <PageHeader
        title={portfolio.name}
        action={
          <Stack direction="row" spacing={1}>
            <Button startIcon={<ArrowBackIcon />} onClick={() => navigate('/portfolios')}>
              Back
            </Button>
            <Button variant="outlined" startIcon={<AnalyticsOutlinedIcon />} onClick={() => navigate('/analytics')}>
              Analytics
            </Button>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => {
                setEditingHolding(null);
                setHoldingFormError('');
                setDialogOpen(true);
              }}
            >
              Add holding
            </Button>
          </Stack>
        }
      />
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ mb: 3 }}>
        <MetricCard label="Current value" value={formatCurrency(portfolio.totalValue)} />
        <MetricCard label="Invested" value={formatCurrency(totalInvested)} />
        <MetricCard label="Profit / loss" value={formatCurrency(portfolio.totalProfitLoss)} color={getSignedColor(portfolio.totalProfitLoss)} />
        <MetricCard label="Return" value={formatRatioPercent(totalReturn)} color={getSignedColor(totalReturn)} />
      </Stack>
      {!holdings.length ? (
        <EmptyState
          title="No holdings"
          message="Add a ticker, quantity, and buy price to track this portfolio's performance."
          action={
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => {
                setHoldingFormError('');
                setDialogOpen(true);
              }}
            >
              Add holding
            </Button>
          }
        />
      ) : (
        <Stack spacing={3}>
          <Stack direction={{ xs: 'column', lg: 'row' }} spacing={3}>
            <ChartCard title="Weight distribution">
              <AllocationPieChart holdings={analytics?.holdings ?? holdings} />
            </ChartCard>
            <ChartCard title="Current value per holding">
              <HoldingsBarChart holdings={analytics?.holdings ?? holdings} metric="currentValue" />
            </ChartCard>
          </Stack>
          <Card>
            <CardContent>
              <HoldingsTable
                holdings={holdings}
                onEdit={(holding) => {
                  setEditingHolding(holding);
                  setHoldingFormError('');
                  setDialogOpen(true);
                }}
                onDelete={setDeletingHolding}
              />
            </CardContent>
          </Card>
        </Stack>
      )}
      <HoldingDialog
        open={dialogOpen}
        holding={editingHolding}
        loading={isSaving}
        errorMessage={holdingFormError}
        onClearError={() => setHoldingFormError('')}
        onClose={() => {
          setDialogOpen(false);
          setEditingHolding(null);
          setHoldingFormError('');
        }}
        onSubmit={submitHolding}
      />
      <ConfirmDialog
        open={Boolean(deletingHolding)}
        title="Delete holding"
        description={`Remove ${deletingHolding?.ticker ?? 'this holding'} from the portfolio?`}
        confirmLabel={deleteHoldingMutation.isPending ? 'Deleting' : 'Delete'}
        destructive
        onClose={() => setDeletingHolding(null)}
        onConfirm={() => deletingHolding && deleteHoldingMutation.mutate(deletingHolding.id)}
      />
    </>
  );
}
