import { apiClient } from './axiosClient';
import type {
  ApiId,
  CreateHoldingRequestDTO,
  CreatePortfolioRequestDTO,
  HoldingResponseDTO,
  PortfolioResponseDTO,
  UpdateHoldingRequestDTO,
  UpdatePortfolioRequestDTO,
} from '../types/api';

export const portfolioApi = {
  getPortfolios: async () => {
    const { data } = await apiClient.get<PortfolioResponseDTO[]>('/api/v1/portfolios');
    return data;
  },
  getPortfolio: async (portfolioId: ApiId) => {
    const { data } = await apiClient.get<PortfolioResponseDTO>(`/api/v1/portfolios/${portfolioId}`);
    return data;
  },
  createPortfolio: async (payload: CreatePortfolioRequestDTO) => {
    const { data } = await apiClient.post<PortfolioResponseDTO>('/api/v1/portfolios', payload);
    return data;
  },
  updatePortfolio: async (portfolioId: ApiId, payload: UpdatePortfolioRequestDTO) => {
    const { data } = await apiClient.patch<PortfolioResponseDTO>(`/api/v1/portfolios/${portfolioId}`, payload);
    return data;
  },
  deletePortfolio: async (portfolioId: ApiId) => {
    await apiClient.delete(`/api/v1/portfolios/${portfolioId}`);
  },
  getHoldings: async (portfolioId: ApiId) => {
    const { data } = await apiClient.get<HoldingResponseDTO[]>(`/api/v1/portfolios/${portfolioId}/holdings`);
    return data;
  },
  createHolding: async (portfolioId: ApiId, payload: CreateHoldingRequestDTO) => {
    const { data } = await apiClient.post<HoldingResponseDTO>(`/api/v1/portfolios/${portfolioId}/holdings`, payload);
    return data;
  },
  updateHolding: async (holdingId: ApiId, payload: UpdateHoldingRequestDTO) => {
    const { data } = await apiClient.patch<HoldingResponseDTO>(`/api/v1/holdings/${holdingId}`, payload);
    return data;
  },
  deleteHolding: async (holdingId: ApiId) => {
    await apiClient.delete(`/api/v1/holdings/${holdingId}`);
  },
};
