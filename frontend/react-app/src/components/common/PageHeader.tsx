import { Box, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

interface PageHeaderProps {
  title: string;
  action?: ReactNode;
}

export function PageHeader({ title, action }: PageHeaderProps) {
  return (
    <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" gap={2} sx={{ mb: 3 }}>
      <Box>
        <Typography variant="h4">{title}</Typography>
      </Box>
      {action && <Box sx={{ alignSelf: { xs: 'stretch', md: 'center' } }}>{action}</Box>}
    </Stack>
  );
}
