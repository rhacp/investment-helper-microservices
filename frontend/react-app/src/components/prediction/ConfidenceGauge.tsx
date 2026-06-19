import { Box, CircularProgress, Stack, Typography } from '@mui/material';
import { formatPercent } from '../../utils/formatters';

export function ConfidenceGauge({ value }: { value?: number }) {
  const normalized = Math.max(0, Math.min(100, Number(value ?? 0)));

  return (
    <Stack alignItems="center" spacing={1.5}>
      <Box sx={{ position: 'relative', display: 'inline-flex' }}>
        <CircularProgress variant="determinate" value={100} size={156} thickness={4} sx={{ color: 'rgba(148, 163, 184, 0.18)' }} />
        <CircularProgress
          variant="determinate"
          value={normalized}
          size={156}
          thickness={4}
          sx={{ color: normalized >= 70 ? 'success.main' : 'warning.main', position: 'absolute', left: 0 }}
        />
        <Box sx={{ inset: 0, position: 'absolute', display: 'grid', placeItems: 'center' }}>
          <Typography variant="h4">{formatPercent(normalized)}</Typography>
        </Box>
      </Box>
      <Typography color="text.secondary">Model confidence</Typography>
    </Stack>
  );
}
