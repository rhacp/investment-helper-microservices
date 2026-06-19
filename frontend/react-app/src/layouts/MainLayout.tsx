import AccountCircleOutlinedIcon from '@mui/icons-material/AccountCircleOutlined';
import AnalyticsOutlinedIcon from '@mui/icons-material/AnalyticsOutlined';
import AutoGraphOutlinedIcon from '@mui/icons-material/AutoGraphOutlined';
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';
import MenuIcon from '@mui/icons-material/Menu';
import PieChartOutlineIcon from '@mui/icons-material/PieChartOutline';
import QueryStatsOutlinedIcon from '@mui/icons-material/QueryStatsOutlined';
import {
  AppBar,
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Stack,
  Toolbar,
  Typography,
  useMediaQuery,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { useState } from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const drawerWidth = 264;

const navSections = [
  {
    label: 'MARKET',
    items: [
      { label: 'Investment Dashboard', path: '/dashboard', icon: <DashboardOutlinedIcon /> },
      { label: 'Portfolios', path: '/portfolios', icon: <PieChartOutlineIcon /> },
      { label: 'Market Data', path: '/stocks', icon: <QueryStatsOutlinedIcon /> },
    ],
  },
  {
    label: 'INTELLIGENCE',
    items: [
      { label: 'Predictions', path: '/predictions', icon: <AutoGraphOutlinedIcon /> },
      { label: 'Analytics', path: '/analytics', icon: <AnalyticsOutlinedIcon /> },
    ],
  },
  {
    label: 'ACCOUNT',
    items: [{ label: 'Profile', path: '/profile', icon: <AccountCircleOutlinedIcon /> }],
  },
];

function Navigation({ onNavigate }: { onNavigate?: () => void }) {
  const navigate = useNavigate();
  const { logout } = useAuth();

  return (
    <Stack sx={{ height: '100%' }}>
      <Box sx={{ px: 3, py: 2.5 }}>
        <Typography variant="h6" sx={{ letterSpacing: 0, fontWeight: 800 }}>
          Investment Helper
        </Typography>
      </Box>
      <Divider />
      <List sx={{ px: 1.5, py: 2 }}>
        {navSections.map((section) => (
          <Box key={section.label} sx={{ mb: 2 }}>
            <Typography
              variant="caption"
              sx={{
                display: 'block',
                px: 1.5,
                pb: 0.75,
                color: 'rgba(216, 180, 254, 0.7)',
                fontWeight: 800,
                letterSpacing: 0.8,
              }}
            >
              {section.label}
            </Typography>
            {section.items.map((item) => (
              <ListItemButton
                key={item.path}
                component={NavLink}
                to={item.path}
                onClick={onNavigate}
                sx={{
                  mb: 0.5,
                  borderRadius: 1,
                  color: 'text.secondary',
                  border: '1px solid transparent',
                  '&:hover': {
                    color: 'text.primary',
                    backgroundColor: 'rgba(139, 92, 246, 0.08)',
                    borderColor: 'rgba(139, 92, 246, 0.16)',
                  },
                  '&.active': {
                    color: 'primary.light',
                    backgroundColor: 'rgba(124, 58, 237, 0.14)',
                    borderColor: 'rgba(139, 92, 246, 0.28)',
                  },
                }}
              >
                <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: 700 }} />
              </ListItemButton>
            ))}
          </Box>
        ))}
      </List>
      <Box sx={{ flex: 1 }} />
      <Divider />
      <Box sx={{ p: 1.5 }}>
        <ListItemButton
          onClick={() => {
            logout();
            navigate('/login');
          }}
          sx={{
            borderRadius: 1,
            color: 'text.secondary',
            '&:hover': {
              color: 'text.primary',
              backgroundColor: 'rgba(139, 92, 246, 0.08)',
            },
          }}
        >
          <ListItemIcon sx={{ minWidth: 40, color: 'inherit' }}>
            <LogoutOutlinedIcon />
          </ListItemIcon>
          <ListItemText primary="Sign out" primaryTypographyProps={{ fontWeight: 700 }} />
        </ListItemButton>
      </Box>
    </Stack>
  );
}

export function MainLayout() {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up('lg'));
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <Box
      sx={{
        minHeight: '100vh',
        background:
          'radial-gradient(circle at top left, rgba(124, 58, 237, 0.16), transparent 34rem), linear-gradient(180deg, #0B0618 0%, #10091F 52%, #0B0618 100%)',
      }}
    >
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          display: { lg: 'none' },
          background: 'rgba(11, 6, 24, 0.92)',
          backdropFilter: 'blur(12px)',
          borderBottom: '1px solid rgba(196, 181, 253, 0.14)',
        }}
      >
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => setMobileOpen(true)} aria-label="open navigation">
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" sx={{ ml: 1 }}>
            Investment Helper
          </Typography>
        </Toolbar>
      </AppBar>
      <Box component="nav" sx={{ width: { lg: drawerWidth }, flexShrink: { lg: 0 } }}>
        <Drawer
          variant={isDesktop ? 'permanent' : 'temporary'}
          open={isDesktop || mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              background: 'linear-gradient(180deg, #120C25 0%, #0B0618 100%)',
              borderRight: '1px solid rgba(196, 181, 253, 0.13)',
            },
          }}
        >
          <Navigation onNavigate={() => setMobileOpen(false)} />
        </Drawer>
      </Box>
      <Box component="main" sx={{ ml: { lg: `${drawerWidth}px` }, pt: { xs: 9, lg: 0 }, minHeight: '100vh' }}>
        <Box sx={{ width: '100%', maxWidth: 1440, mx: 'auto', px: { xs: 2, sm: 3, xl: 4 }, py: { xs: 2, lg: 4 } }}>
          <Outlet />
        </Box>
      </Box>
    </Box>
  );
}
