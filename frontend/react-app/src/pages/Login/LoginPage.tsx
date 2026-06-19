import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import LoginOutlinedIcon from '@mui/icons-material/LoginOutlined';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Link,
  Stack,
  TextField,
  Typography,
} from '@mui/material';
import { useMutation } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Link as RouterLink, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { getApiErrorMessage } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';
import { consumeSessionMessage } from '../../utils/authSession';

const schema = z.object({
  email: z.string().email('Enter a valid email'),
  password: z.string().min(1, 'Password is required'),
});

type LoginValues = z.infer<typeof schema>;

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const form = useForm<LoginValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: '', password: '' },
  });

  useEffect(() => {
    consumeSessionMessage();
  }, []);

  const mutation = useMutation({
    mutationFn: login,
    onSuccess: () => {
      const target = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/dashboard';
      navigate(target, { replace: true });
    },
  });

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'grid',
        placeItems: 'center',
        px: 2,
        background:
          'radial-gradient(circle at 22% 0%, rgba(124, 58, 237, 0.2), transparent 32rem), linear-gradient(180deg, #0B0618 0%, #10091F 100%)',
      }}
    >
      <Container maxWidth="sm">
        <Stack spacing={3}>
          <Box>
            <Stack direction="row" spacing={1.5} alignItems="center">
              <Box sx={{ display: 'grid', placeItems: 'center', width: 44, height: 44, borderRadius: 2, bgcolor: 'primary.main' }}>
                <LockOutlinedIcon />
              </Box>
              <Box>
                <Typography variant="h4">Investment Helper</Typography>
              </Box>
            </Stack>
          </Box>
          <Card>
            <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
              <Stack component="form" spacing={2.5} onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
                <TextField
                  label="Email"
                  type="email"
                  autoComplete="email"
                  {...form.register('email')}
                  error={Boolean(form.formState.errors.email)}
                  helperText={form.formState.errors.email?.message}
                />
                <TextField
                  label="Password"
                  type="password"
                  autoComplete="current-password"
                  {...form.register('password')}
                  error={Boolean(form.formState.errors.password)}
                  helperText={form.formState.errors.password?.message}
                />
                {mutation.isError && <Alert severity="error">{getApiErrorMessage(mutation.error, 'Could not sign in')}</Alert>}
                <Button type="submit" variant="contained" size="large" startIcon={<LoginOutlinedIcon />} disabled={mutation.isPending}>
                  {mutation.isPending ? 'Signing in' : 'Sign in'}
                </Button>
                <Typography color="text.secondary" textAlign="center">
                  No account?{' '}
                  <Link component={RouterLink} to="/register" color="primary.main" fontWeight={800}>
                    Create one
                  </Link>
                </Typography>
              </Stack>
            </CardContent>
          </Card>
        </Stack>
      </Container>
    </Box>
  );
}
