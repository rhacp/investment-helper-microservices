import { authApi } from '../api/authApi';
import type { LoginRequestDTO, RegisterRequestDTO } from '../types/api';
import { clearAuthToken, getAuthToken, setAuthToken } from '../utils/authSession';

export const authService = {
  login: async (payload: LoginRequestDTO) => {
    const response = await authApi.login(payload);
    const token = response.accessToken ?? response.signature;
    if (!token) {
      throw new Error('Could not complete sign in. Please try again.');
    }
    setAuthToken(token);
    return { ...response, accessToken: token };
  },
  register: (payload: RegisterRequestDTO) => authApi.register(payload),
  logout: clearAuthToken,
  getToken: getAuthToken,
};
