import { Card, CardContent, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

interface MetricCardProps {
  label: string;
  value: ReactNode;
  helper?: ReactNode;
  description?: ReactNode;
  icon?: ReactNode;
  color?: string;
}

export function MetricCard({ label, value, helper, description, icon, color = 'text.primary' }: MetricCardProps) {
  return (
    <Card sx={{ alignSelf: 'stretch', display: 'flex', flex: 1, minHeight: 128, minWidth: 0 }}>
      <CardContent sx={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
        <Stack direction="row" alignItems="center" justifyContent="space-between" gap={2}>
          <Typography variant="body2" color="text.secondary" sx={{ fontWeight: 700 }}>
            {label}
          </Typography>
          {icon}
        </Stack>
        <Typography variant="h5" sx={{ mt: 1, color, overflowWrap: 'anywhere' }}>
          {value}
        </Typography>
        {description && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.75 }}>
            {description}
          </Typography>
        )}
        <Stack sx={{ flex: 1 }} />
        {helper && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: description ? 1 : 0.75 }}>
            {helper}
          </Typography>
        )}
      </CardContent>
    </Card>
  );
}
