import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { Box, Stack, Typography } from '@mui/material';

export function ErrorMessage({ title = 'Request failed', message }: { title?: string; message: string }) {
  return (
    <Box
      sx={{
        border: '1px solid rgba(196, 181, 253, 0.18)',
        borderRadius: 2,
        background: 'linear-gradient(135deg, rgba(124, 58, 237, 0.1), rgba(21, 16, 40, 0.82))',
        boxShadow: '0 18px 40px rgba(10, 7, 22, 0.22)',
        px: { xs: 2, sm: 2.5 },
        py: 2,
      }}
    >
      <Stack direction="row" spacing={1.5} alignItems="flex-start">
        <Box
          sx={{
            color: 'primary.light',
            display: 'grid',
            placeItems: 'center',
            mt: 0.25,
          }}
        >
          <InfoOutlinedIcon fontSize="small" />
        </Box>
        <Box>
          <Typography variant="subtitle1" fontWeight={800}>
            {title}
          </Typography>
          <Typography color="text.secondary" sx={{ whiteSpace: 'pre-line' }}>
            {message}
          </Typography>
        </Box>
      </Stack>
    </Box>
  );
}
