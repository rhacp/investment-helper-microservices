import { apiClient } from './axiosClient';
import type { ApiId, UserDTO, UserUpdateDTO } from '../types/api';

export const userApi = {
  getProfile: async (authUserId: ApiId) => {
    const { data } = await apiClient.get<UserDTO>(`/api/v1/users/${authUserId}`);
    return data;
  },
  updateProfile: async (authUserId: ApiId, payload: UserUpdateDTO) => {
    const { data } = await apiClient.patch<UserDTO>(`/api/v1/users/${authUserId}`, payload);
    return data;
  },
};
