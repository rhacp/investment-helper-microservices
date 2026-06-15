import { apiClient } from './axiosClient';
import type { LoginRequestDTO, LoginResponseDTO, RegisterRequestDTO, RegisterResponseDTO } from '../types/api';

export const authApi = {
  login: async (payload: LoginRequestDTO) => {
    const { data } = await apiClient.post<LoginResponseDTO>('/api/v1/auth/login', payload);
    return data;
  },
  register: async (payload: RegisterRequestDTO) => {
    const { data } = await apiClient.post<RegisterResponseDTO>('/api/v1/auth/register', payload);
    return data;
  },
};
