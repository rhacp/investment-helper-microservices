import { Box, CircularProgress, Stack, Typography } from '@mui/material';

export function LoadingSpinner({ label = 'Loading market data' }: { label?: string }) {
  return (
    <Box sx={{ display: 'grid', placeItems: 'center', minHeight: 240 }}>
      <Stack alignItems="center" gap={2}>
        <CircularProgress />
        <Typography color="text.secondary">{label}</Typography>
      </Stack>
    </Box>
  );
}
