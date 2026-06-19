import InboxOutlinedIcon from '@mui/icons-material/InboxOutlined';
import { Box, Stack, Typography } from '@mui/material';
import type { ReactNode } from 'react';

interface EmptyStateProps {
  title: string;
  message: string;
  action?: ReactNode;
}

export function EmptyState({ title, message, action }: EmptyStateProps) {
  return (
    <Box sx={{ border: '1px dashed rgba(148, 163, 184, 0.28)', borderRadius: 2, py: 7, px: 3 }}>
      <Stack alignItems="center" spacing={1.5} textAlign="center">
        <InboxOutlinedIcon color="disabled" fontSize="large" />
        <Typography variant="h6">{title}</Typography>
        <Typography color="text.secondary" sx={{ maxWidth: 520 }}>
          {message}
        </Typography>
        {action}
      </Stack>
    </Box>
  );
}
