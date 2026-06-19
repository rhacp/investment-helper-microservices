import axios, { AxiosError } from 'axios';
import type { ErrorDTO } from '../types/api';
import { getAuthToken, notifySessionExpired, SESSION_EXPIRED_MESSAGE } from '../utils/authSession';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL ?? 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

function isAuthEndpoint(url?: string) {
  return Boolean(url?.includes('/api/v1/auth/login') || url?.includes('/api/v1/auth/register'));
}

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ErrorDTO>) => {
    const status = error.response?.status;
    const hasStoredToken = Boolean(getAuthToken());

    if (status === 401 && hasStoredToken && !isAuthEndpoint(error.config?.url)) {
      notifySessionExpired();
      if (window.location.pathname !== '/login') {
        window.location.assign('/login');
      }
      return Promise.reject(new Error(SESSION_EXPIRED_MESSAGE));
    }

    return Promise.reject(error);
  },
);

export function getApiErrorMessage(error: unknown, fallback = 'Something went wrong') {
  if (axios.isAxiosError<ErrorDTO>(error)) {
    if (error.response?.status === 401) {
      return fallback;
    }
    const body = error.response?.data;
    const firstFieldError = body?.errors ? Object.values(body.errors).flat()[0] : undefined;
    return firstFieldError ?? body?.message ?? error.message ?? fallback;
  }
  return fallback;
}
