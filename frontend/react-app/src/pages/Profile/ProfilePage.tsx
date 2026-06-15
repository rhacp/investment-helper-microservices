import SaveOutlinedIcon from '@mui/icons-material/SaveOutlined';
import { zodResolver } from '@hookform/resolvers/zod';
import { Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-toastify';
import { z } from 'zod';
import { ErrorMessage } from '../../components/common/ErrorMessage';
import { LoadingSpinner } from '../../components/common/LoadingSpinner';
import { PageHeader } from '../../components/common/PageHeader';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../services/userService';

const schema = z.object({
  email: z.string().email('Enter a valid email'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  dateOfBirth: z.string().min(1, 'Date of birth is required'),
});

type ProfileValues = z.infer<typeof schema>;

export function ProfilePage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const authUserId = user?.authUserId;
  const form = useForm<ProfileValues>({
    resolver: zodResolver(schema),
    defaultValues: { email: '', firstName: '', lastName: '', dateOfBirth: '' },
  });

  const profileQuery = useQuery({
    queryKey: ['profile', authUserId],
    queryFn: () => userService.getProfile(Number(authUserId)),
    enabled: Boolean(authUserId),
    retry: false,
  });

  useEffect(() => {
    if (profileQuery.data) {
      form.reset({
        email: profileQuery.data.email,
        firstName: profileQuery.data.firstName,
        lastName: profileQuery.data.lastName,
        dateOfBirth: profileQuery.data.dateOfBirth,
      });
    }
  }, [form, profileQuery.data]);

  const updateMutation = useMutation({
    mutationFn: (values: ProfileValues) => userService.updateProfile(Number(authUserId), values),
    onSuccess: () => {
      toast.success('Profile updated');
      queryClient.invalidateQueries({ queryKey: ['profile', authUserId] });
    },
  });

  return (
    <>
      <PageHeader eyebrow="Account" title="Profile" subtitle="Review and update the user profile linked to the authenticated account." />
      {!authUserId ? (
        <ErrorMessage message="We could not load your profile. Please sign in again." />
      ) : profileQuery.isLoading ? (
        <LoadingSpinner label="Loading profile" />
      ) : profileQuery.isError ? (
        <ErrorMessage message="Could not load your profile." />
      ) : (
        <Card>
          <CardContent>
            <Stack component="form" spacing={2.5} maxWidth={720} onSubmit={form.handleSubmit((values) => updateMutation.mutate(values))}>
              <Typography variant="h6">Personal information</Typography>
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
                label="Date of birth"
                type="date"
                InputLabelProps={{ shrink: true }}
                {...form.register('dateOfBirth')}
                error={Boolean(form.formState.errors.dateOfBirth)}
                helperText={form.formState.errors.dateOfBirth?.message}
              />
              <Button type="submit" variant="contained" startIcon={<SaveOutlinedIcon />} disabled={updateMutation.isPending} sx={{ alignSelf: 'flex-start' }}>
                {updateMutation.isPending ? 'Saving' : 'Save profile'}
              </Button>
            </Stack>
          </CardContent>
        </Card>
      )}
    </>
  );
}
