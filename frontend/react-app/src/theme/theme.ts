import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    mode: 'dark',
    background: {
      default: '#0B0618',
      paper: '#151028',
    },
    primary: {
      main: '#7C3AED',
      light: '#8B5CF6',
      dark: '#5B21B6',
    },
    secondary: {
      main: '#C084FC',
    },
    success: {
      main: '#22C55E',
    },
    error: {
      main: '#EF4444',
    },
    warning: {
      main: '#F59E0B',
    },
    text: {
      primary: '#F9FAFB',
      secondary: '#9CA3AF',
    },
    divider: 'rgba(148, 163, 184, 0.18)',
  },
  shape: {
    borderRadius: 8,
  },
  typography: {
    fontFamily: '"Manrope", "Plus Jakarta Sans", ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    h1: { fontWeight: 800, letterSpacing: 0 },
    h2: { fontWeight: 800, letterSpacing: 0 },
    h3: { fontWeight: 800, letterSpacing: 0 },
    h4: { fontWeight: 800, letterSpacing: 0 },
    h5: { fontWeight: 750, letterSpacing: 0 },
    h6: { fontWeight: 750, letterSpacing: 0 },
    button: { textTransform: 'none', fontWeight: 700 },
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundImage: 'linear-gradient(180deg, rgba(255, 255, 255, 0.035), rgba(255, 255, 255, 0))',
          backgroundColor: '#151028',
          border: '1px solid rgba(196, 181, 253, 0.13)',
          boxShadow: '0 12px 34px rgba(4, 1, 14, 0.28)',
          transition: 'border-color 160ms ease, box-shadow 160ms ease, background-color 160ms ease',
          '&:hover': {
            borderColor: 'rgba(139, 92, 246, 0.34)',
            boxShadow: '0 18px 42px rgba(65, 31, 128, 0.22)',
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
        },
        containedPrimary: {
          boxShadow: '0 10px 24px rgba(124, 58, 237, 0.24)',
          '&:hover': {
            backgroundColor: '#8B5CF6',
            boxShadow: '0 12px 28px rgba(139, 92, 246, 0.28)',
          },
        },
        outlinedPrimary: {
          borderColor: 'rgba(139, 92, 246, 0.48)',
          '&:hover': {
            borderColor: '#8B5CF6',
            backgroundColor: 'rgba(139, 92, 246, 0.08)',
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderColor: 'rgba(196, 181, 253, 0.12)',
        },
        head: {
          color: '#D8B4FE',
          fontSize: 12,
          fontWeight: 800,
          textTransform: 'uppercase',
        },
      },
    },
  },
});
