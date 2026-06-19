import { zodResolver } from '@hookform/resolvers/zod';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Stack, TextField, Typography } from '@mui/material';
import dayjs from 'dayjs';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import type { HoldingResponseDTO } from '../../types/api';
import { normalizeTicker } from '../../utils/formatters';

const schema = z.object({
  ticker: z.string().min(1, 'Ticker is required'),
  quantity: z.coerce.number().positive('Quantity must be positive'),
  averageBuyPrice: z.coerce.number().positive('Average buy price must be positive'),
  purchaseDate: z.string().optional(),
});

export type HoldingFormValues = z.infer<typeof schema>;

interface HoldingDialogProps {
  open: boolean;
  holding?: HoldingResponseDTO | null;
  loading?: boolean;
  errorMessage?: string;
  onClearError?: () => void;
  onClose: () => void;
  onSubmit: (values: HoldingFormValues) => void;
}

export function HoldingDialog({ open, holding, loading, errorMessage, onClearError, onClose, onSubmit }: HoldingDialogProps) {
  const form = useForm<HoldingFormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      ticker: '',
      quantity: 0,
      averageBuyPrice: 0,
      purchaseDate: dayjs().format('YYYY-MM-DD'),
    },
  });

  useEffect(() => {
    form.reset({
      ticker: holding?.ticker ?? '',
      quantity: holding?.quantity ?? 0,
      averageBuyPrice: holding?.averageBuyPrice ?? 0,
      purchaseDate: dayjs().format('YYYY-MM-DD'),
    });
  }, [form, holding, open]);

  const tickerField = form.register('ticker', {
    onChange: () => onClearError?.(),
  });

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{holding ? 'Edit holding' : 'Add holding'}</DialogTitle>
      <Stack component="form" onSubmit={form.handleSubmit((values) => onSubmit({ ...values, ticker: normalizeTicker(values.ticker) }))}>
        <DialogContent>
          <Stack spacing={2}>
            <TextField
              label="Ticker"
              disabled={Boolean(holding)}
              {...tickerField}
              error={Boolean(form.formState.errors.ticker)}
              helperText={form.formState.errors.ticker?.message}
            />
            {errorMessage && (
              <Box
                sx={{
                  border: '1px solid rgba(196, 181, 253, 0.18)',
                  borderRadius: 2,
                  background: 'rgba(124, 58, 237, 0.08)',
                  px: 2,
                  py: 1.5,
                }}
              >
                <Stack direction="row" spacing={1.25} alignItems="flex-start">
                  <InfoOutlinedIcon fontSize="small" sx={{ color: 'primary.light', mt: 0.25 }} />
                  <Box>
                    <Typography variant="subtitle2" fontWeight={800}>
                      Ticker not available
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {errorMessage}
                    </Typography>
                  </Box>
                </Stack>
              </Box>
            )}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                label="Quantity"
                type="number"
                fullWidth
                inputProps={{ min: 0, step: '0.0001' }}
                {...form.register('quantity')}
                error={Boolean(form.formState.errors.quantity)}
                helperText={form.formState.errors.quantity?.message}
              />
              <TextField
                label="Average buy price"
                type="number"
                fullWidth
                inputProps={{ min: 0, step: '0.01' }}
                {...form.register('averageBuyPrice')}
                error={Boolean(form.formState.errors.averageBuyPrice)}
                helperText={form.formState.errors.averageBuyPrice?.message}
              />
            </Stack>
            <TextField label="Purchase date" type="date" InputLabelProps={{ shrink: true }} {...form.register('purchaseDate')} />
          </Stack>
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
