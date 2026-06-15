import AddIcon from '@mui/icons-material/Add';
import AccountBalanceWalletOutlinedIcon from '@mui/icons-material/AccountBalanceWalletOutlined';
import ShowChartOutlinedIcon from '@mui/icons-material/ShowChartOutlined';
import TrendingDownOutlinedIcon from '@mui/icons-material/TrendingDownOutlined';
import TrendingUpOutlinedIcon from '@mui/icons-material/TrendingUpOutlined';
import { Button, Card, CardContent, Stack } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { AllocationPieChart } from '../../components/charts/AllocationPieChart';
import { ChartCard } from '../../components/charts/ChartCard';
import { HoldingsBarChart } from '../../components/charts/HoldingsBarChart';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { MetricCard } from '../../components/common/MetricCard';
import { PageHeader } from '../../components/common/PageHeader';
import { PortfolioTable } from '../../components/portfolio/PortfolioTable';
import { portfolioService } from '../../services/portfolioService';
import type { PortfolioResponseDTO } from '../../types/api';
import { formatCurrency, getSignedColor } from '../../utils/formatters';

export function DashboardPage() {
  const navigate = useNavigate();
  const portfoliosQuery = useQuery({ queryKey: ['portfolios'], queryFn: portfolioService.getPortfolios });
  const portfolios: PortfolioResponseDTO[] = portfoliosQuery.data ?? [];
  const holdings = portfolios.flatMap((portfolio) => portfolio.holdings ?? []);
  const totalValue = portfolios.reduce((sum, portfolio) => sum + Number(portfolio.totalValue ?? 0), 0);
  const totalProfitLoss = portfolios.reduce((sum, portfolio) => sum + Number(portfolio.totalProfitLoss ?? 0), 0);
  const ranked = [...portfolios].sort((a, b) => Number(b.totalProfitLoss ?? 0) - Number(a.totalProfitLoss ?? 0));

  if (portfoliosQuery.isLoading) return <LoadingSpinner label="Loading dashboard" />;
  if (portfoliosQuery.isError) return <ErrorMessage message="Could not load portfolios from the API gateway." />;

  return (
    <>
      <PageHeader
        eyebrow="Overview"
        title="Portfolio Overview"
        subtitle="A consolidated view of portfolio value, allocation, and performance."
        action={
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/portfolios')}>
            Manage portfolios
          </Button>
        }
      />
      <Stack direction={{ xs: 'column', md: 'row' }} spacing={2} sx={{ alignItems: 'stretch', mb: 3 }}>
        <MetricCard
          label="Portfolios"
          value={portfolios.length}
          description="Active investment portfolios tracked in the platform."
          icon={<AccountBalanceWalletOutlinedIcon color="primary" />}
        />
        <MetricCard
          label="Current value"
          value={formatCurrency(totalValue)}
          description="Combined market value across all holdings."
          icon={<ShowChartOutlinedIcon color="primary" />}
        />
        <MetricCard
          label="Total profit / loss"
          value={formatCurrency(totalProfitLoss)}
          description="Unrealized performance based on current prices."
          color={getSignedColor(totalProfitLoss)}
          icon={totalProfitLoss >= 0 ? <TrendingUpOutlinedIcon color="success" /> : <TrendingDownOutlinedIcon color="error" />}
        />
        <MetricCard
          label="Best performer"
          value={ranked[0]?.name ?? 'None'}
          description="Portfolio with the strongest absolute P/L."
          helper={ranked[0] ? formatCurrency(ranked[0].totalProfitLoss) : undefined}
        />
      </Stack>
      {!portfolios.length ? (
        <EmptyState
          title="No portfolios yet"
          message="Create your first portfolio and add holdings to unlock allocation, risk, and ML prediction workflows."
          action={
            <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/portfolios')}>
              Create portfolio
            </Button>
          }
        />
      ) : (
        <Stack spacing={3}>
          <Stack direction={{ xs: 'column', lg: 'row' }} spacing={3}>
            <ChartCard title="Portfolio allocation">
              <AllocationPieChart holdings={holdings} />
            </ChartCard>
            <ChartCard title="Profit / loss per holding">
              <HoldingsBarChart holdings={holdings} metric="profitLoss" />
            </ChartCard>
          </Stack>
          <Card>
            <CardContent>
              <PortfolioTable
                portfolios={portfolios}
                onView={(portfolio) => navigate(`/portfolios/${portfolio.id}`)}
                onEdit={(portfolio) => navigate(`/portfolios/${portfolio.id}`)}
                onDelete={(portfolio) => navigate(`/portfolios/${portfolio.id}`)}
              />
            </CardContent>
          </Card>
        </Stack>
      )}
    </>
  );
}
