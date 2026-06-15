import { apiClient } from './axiosClient';
import type { MarketPriceResponseDTO, StockResponseDTO } from '../types/api';

export const marketDataApi = {
  getStock: async (ticker: string) => {
    const { data } = await apiClient.get<StockResponseDTO>(`/api/v1/stocks/${ticker}`);
    return data;
  },
  getHistory: async (ticker: string) => {
    const { data } = await apiClient.get<MarketPriceResponseDTO[]>(`/api/v1/stocks/${ticker}/history`);
    return data;
  },
};
