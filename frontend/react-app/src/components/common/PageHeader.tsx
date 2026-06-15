import { Box, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

interface PageHeaderProps {
  title: string;
  eyebrow?: string;
  subtitle?: string;
  action?: ReactNode;
}

export function PageHeader({ title, eyebrow, subtitle, action }: PageHeaderProps) {
  return (
    <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" gap={2} sx={{ mb: 3 }}>
      <Box>
        {eyebrow && (
          <Typography variant="overline" color="primary.main" sx={{ fontWeight: 800 }}>
            {eyebrow}
          </Typography>
        )}
        <Typography variant="h4">{title}</Typography>
        {subtitle && (
          <Typography color="text.secondary" sx={{ mt: 0.5, maxWidth: 780 }}>
            {subtitle}
          </Typography>
        )}
      </Box>
      {action && <Box sx={{ alignSelf: { xs: 'stretch', md: 'center' } }}>{action}</Box>}
    </Stack>
  );
}
