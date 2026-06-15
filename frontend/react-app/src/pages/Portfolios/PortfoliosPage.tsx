import AddIcon from '@mui/icons-material/Add';
import { Button, Card, CardContent } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { ConfirmDialog } from '../../components/common/ConfirmDialog';
import { EmptyState } from '../../components/common/EmptyState';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { PageHeader } from '../../components/common/PageHeader';
import { PortfolioDialog, type PortfolioFormValues } from '../../components/portfolio/PortfolioDialog';
import { PortfolioTable } from '../../components/portfolio/PortfolioTable';
import { portfolioService } from '../../services/portfolioService';
import type { PortfolioResponseDTO } from '../../types/api';

export function PortfoliosPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingPortfolio, setEditingPortfolio] = useState<PortfolioResponseDTO | null>(null);
  const [deletingPortfolio, setDeletingPortfolio] = useState<PortfolioResponseDTO | null>(null);

  const portfoliosQuery = useQuery({ queryKey: ['portfolios'], queryFn: portfolioService.getPortfolios });

  const createMutation = useMutation({
    mutationFn: portfolioService.createPortfolio,
    onSuccess: () => {
      toast.success('Portfolio created');
      setDialogOpen(false);
      queryClient.invalidateQueries({ queryKey: ['portfolios'] });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, values }: { id: number; values: PortfolioFormValues }) => portfolioService.updatePortfolio(id, values),
    onSuccess: () => {
      toast.success('Portfolio updated');
      setDialogOpen(false);
      setEditingPortfolio(null);
      queryClient.invalidateQueries({ queryKey: ['portfolios'] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: portfolioService.deletePortfolio,
    onSuccess: () => {
      toast.success('Portfolio deleted');
      setDeletingPortfolio(null);
      queryClient.invalidateQueries({ queryKey: ['portfolios'] });
    },
  });

  const portfolios: PortfolioResponseDTO[] = portfoliosQuery.data ?? [];
  const isSaving = createMutation.isPending || updateMutation.isPending;

  if (portfoliosQuery.isLoading) return <LoadingSpinner label="Loading portfolios" />;
  if (portfoliosQuery.isError) return <ErrorMessage message="Could not load portfolios from the API gateway." />;

  const submitPortfolio = (values: PortfolioFormValues) => {
    if (editingPortfolio) {
      updateMutation.mutate({ id: editingPortfolio.id, values });
      return;
    }
    createMutation.mutate(values);
  };

  return (
    <>
      <PageHeader
        eyebrow="Portfolio management"
        title="Portfolios"
        subtitle="Create portfolios and open detailed holding views backed by the portfolio service."
        action={
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => {
              setEditingPortfolio(null);
              setDialogOpen(true);
            }}
          >
            New portfolio
          </Button>
        }
      />
      {!portfolios.length ? (
        <EmptyState
          title="No portfolios"
          message="Create a portfolio, then add holdings to track value, profit/loss, and allocation."
          action={
            <Button variant="contained" startIcon={<AddIcon />} onClick={() => setDialogOpen(true)}>
              New portfolio
            </Button>
          }
        />
      ) : (
        <Card>
          <CardContent>
            <PortfolioTable
              portfolios={portfolios}
              onView={(portfolio) => navigate(`/portfolios/${portfolio.id}`)}
              onEdit={(portfolio) => {
                setEditingPortfolio(portfolio);
                setDialogOpen(true);
              }}
              onDelete={setDeletingPortfolio}
            />
          </CardContent>
        </Card>
      )}
      <PortfolioDialog
        open={dialogOpen}
        portfolio={editingPortfolio}
        loading={isSaving}
        onClose={() => {
          setDialogOpen(false);
          setEditingPortfolio(null);
        }}
        onSubmit={submitPortfolio}
      />
      <ConfirmDialog
        open={Boolean(deletingPortfolio)}
        title="Delete portfolio"
        description={`Delete ${deletingPortfolio?.name ?? 'this portfolio'} and its holdings?`}
        confirmLabel={deleteMutation.isPending ? 'Deleting' : 'Delete'}
        destructive
        onClose={() => setDeletingPortfolio(null)}
        onConfirm={() => deletingPortfolio && deleteMutation.mutate(deletingPortfolio.id)}
      />
    </>
  );
}
