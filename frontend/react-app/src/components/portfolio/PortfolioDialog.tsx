import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField } from '@mui/material';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import type { PortfolioResponseDTO } from '../../types/api';

const schema = z.object({
  name: z.string().min(2, 'Portfolio name is required'),
});

export type PortfolioFormValues = z.infer<typeof schema>;

interface PortfolioDialogProps {
  open: boolean;
  portfolio?: PortfolioResponseDTO | null;
  loading?: boolean;
  onClose: () => void;
  onSubmit: (values: PortfolioFormValues) => void;
}

export function PortfolioDialog({ open, portfolio, loading, onClose, onSubmit }: PortfolioDialogProps) {
  const form = useForm<PortfolioFormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: '' },
  });

  useEffect(() => {
    form.reset({ name: portfolio?.name ?? '' });
  }, [form, portfolio, open]);

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
      <DialogTitle>{portfolio ? 'Edit portfolio' : 'Create portfolio'}</DialogTitle>
      <Stack component="form" onSubmit={form.handleSubmit(onSubmit)}>
        <DialogContent>
          <TextField
            label="Portfolio name"
            fullWidth
            autoFocus
            {...form.register('name')}
            error={Boolean(form.formState.errors.name)}
            helperText={form.formState.errors.name?.message}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose}>Cancel</Button>
          <Button type="submit" variant="contained" disabled={loading}>
            {loading ? 'Saving' : 'Save'}
          </Button>
        </DialogActions>
      </Stack>
    </Dialog>
  );
}
