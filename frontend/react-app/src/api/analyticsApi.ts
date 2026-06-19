import { apiClient } from './axiosClient';
import type { ApiId, PortfolioAnalyticsResponseDTO, StockAnalyticsResponseDTO } from '../types/api';

export const analyticsApi = {
  getStockAnalytics: async (ticker: string) => {
    const { data } = await apiClient.get<StockAnalyticsResponseDTO>(`/api/v1/analytics/stocks/${ticker}`);
    return data;
  },
  getPortfolioAnalytics: async (portfolioId: ApiId) => {
    const { data } = await apiClient.get<PortfolioAnalyticsResponseDTO>(`/api/v1/analytics/portfolios/${portfolioId}`);
    return data;
  },
};
