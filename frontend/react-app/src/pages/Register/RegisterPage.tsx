import HowToRegOutlinedIcon from '@mui/icons-material/HowToRegOutlined';
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
import dayjs from 'dayjs';
import { useForm } from 'react-hook-form';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { z } from 'zod';
import { getApiErrorMessage } from '../../api/axiosClient';
import { useAuth } from '../../context/AuthContext';

const schema = z.object({
  firstName: z.string().min(2, 'First name is required'),
  lastName: z.string().min(2, 'Last name is required'),
  email: z.string().email('Enter a valid email'),
  password: z.string().min(8, 'Use at least 8 characters'),
  dateOfBirth: z.string().min(1, 'Date of birth is required'),
});

type RegisterValues = z.infer<typeof schema>;

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const form = useForm<RegisterValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      dateOfBirth: dayjs().subtract(25, 'year').format('YYYY-MM-DD'),
    },
  });

  const mutation = useMutation({
    mutationFn: register,
    onSuccess: () => {
      toast.success('Account created. You can sign in now.');
      navigate('/login');
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
            <Typography variant="h4">Create account</Typography>
            <Typography color="text.secondary">Start tracking portfolios, risk, and model predictions</Typography>
          </Box>
          <Card>
            <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
              <Stack component="form" spacing={2.5} onSubmit={form.handleSubmit((values) => mutation.mutate(values))}>
                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                  <TextField
                    label="First name"
                    fullWidth
                    {...form.register('firstName')}
                    error={Boolean(form.formState.errors.firstName)}
                    helperText={form.formState.errors.firstName?.message}
                  />
                  <TextField
                    label="Last name"
                    fullWidth
                    {...form.register('lastName')}
                    error={Boolean(form.formState.errors.lastName)}
                    helperText={form.formState.errors.lastName?.message}
                  />
                </Stack>
                <TextField
                  label="Email"
                  type="email"
                  {...form.register('email')}
                  error={Boolean(form.formState.errors.email)}
                  helperText={form.formState.errors.email?.message}
                />
                <TextField
                  label="Password"
                  type="password"
                  {...form.register('password')}
                  error={Boolean(form.formState.errors.password)}
                  helperText={form.formState.errors.password?.message}
                />
                <TextField
                  label="Date of birth"
                  type="date"
                  InputLabelProps={{ shrink: true }}
                  {...form.register('dateOfBirth')}
                  error={Boolean(form.formState.errors.dateOfBirth)}
                  helperText={form.formState.errors.dateOfBirth?.message}
                />
                {mutation.isError && <Alert severity="error">{getApiErrorMessage(mutation.error, 'Could not create account')}</Alert>}
                <Button type="submit" variant="contained" size="large" startIcon={<HowToRegOutlinedIcon />} disabled={mutation.isPending}>
                  {mutation.isPending ? 'Creating account' : 'Create account'}
                </Button>
                <Typography color="text.secondary" textAlign="center">
                  Already registered?{' '}
                  <Link component={RouterLink} to="/login" color="primary.main" fontWeight={800}>
                    Sign in
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
