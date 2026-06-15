import { apiClient } from './axiosClient';
import type { PredictionAnalyticsResponseDTO, PredictionResponseDTO } from '../types/api';

export const predictionApi = {
  latest: async (ticker: string) => {
    const { data } = await apiClient.get<PredictionResponseDTO>(`/api/v1/predictions/${ticker}/latest`);
    return data;
  },
  analytics: async (ticker: string) => {
    const { data } = await apiClient.get<PredictionAnalyticsResponseDTO>(`/api/v1/predictions/${ticker}/analytics`);
    return data;
  },
};
