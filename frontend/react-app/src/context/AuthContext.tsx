import { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react';
import { authService } from '../services/authService';
import type { LoginRequestDTO, RegisterRequestDTO } from '../types/api';
import { SESSION_EXPIRED_EVENT } from '../utils/authSession';

interface AuthUser {
  email?: string;
  role?: string;
  authUserId?: number;
}

interface AuthContextValue {
  token: string | null;
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (payload: LoginRequestDTO) => Promise<void>;
  register: (payload: RegisterRequestDTO) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function decodeJwt(token: string | null): AuthUser | null {
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1] ?? '')) as Record<string, unknown>;
    const subject = Number(payload.sub);
    return {
      email: String(payload.email ?? ''),
      role: Array.isArray(payload.roles) ? String(payload.roles[0]) : String(payload.role ?? ''),
      authUserId: Number.isFinite(subject) && subject > 0 ? subject : undefined,
    };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => authService.getToken());

  useEffect(() => {
    const clearAuthState = () => setToken(null);
    window.addEventListener(SESSION_EXPIRED_EVENT, clearAuthState);
    return () => window.removeEventListener(SESSION_EXPIRED_EVENT, clearAuthState);
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    token,
    user: decodeJwt(token),
    isAuthenticated: Boolean(token),
    login: async (payload) => {
      const response = await authService.login(payload);
      setToken(response.accessToken ?? null);
    },
    register: async (payload) => {
      await authService.register(payload);
    },
    logout: () => {
      authService.logout();
      setToken(null);
    },
  }), [token]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return value;
}
