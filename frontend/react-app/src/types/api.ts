export type ApiId = number;

export interface ErrorDTO {
  message?: string;
  errors?: Record<string, string[]>;
  path?: string;
  timestamp?: string;
}

export interface LoginRequestDTO {
  email: string;
  password: string;
}

export interface LoginResponseDTO {
  accessToken?: string;
  signature?: string;
  tokenType?: string;
  expiresIn?: number;
}

export interface RegisterRequestDTO {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
}

export interface RegisterResponseDTO {
  id: ApiId;
  email: string;
  role: string;
  enabled: boolean;
}

export interface UserDTO {
  id: ApiId;
  authUserId: ApiId;
  email: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  active: boolean;
}

export interface UserUpdateDTO {
  email?: string;
  firstName?: string;
  lastName?: string;
  dateOfBirth?: string;
}

export interface StockResponseDTO {
  id: ApiId;
  ticker: string;
  name: string;
  exchange: string;
  sector?: string;
  currency: string;
}

export interface StockTickerResponseDTO {
  ticker: string;
}

export interface MarketPriceResponseDTO {
  date?: string;
  priceDate?: string;
  openPrice: number;
  highPrice: number;
  lowPrice: number;
  closePrice: number;
  volume: number;
}

export interface HoldingResponseDTO {
  id: ApiId;
  ticker: string;
  quantity: number;
  averageBuyPrice: number;
  currentPrice: number;
  profitLoss: number;
  currentValue: number;
  profitPercentage: number;
}

export interface PortfolioResponseDTO {
  id: ApiId;
  name: string;
  totalValue: number;
  totalProfitLoss: number;
  holdings: HoldingResponseDTO[];
}

export interface CreatePortfolioRequestDTO {
  name: string;
}

export interface UpdatePortfolioRequestDTO {
  name?: string;
}

export interface CreateHoldingRequestDTO {
  ticker: string;
  quantity: number;
  averageBuyPrice: number;
  purchaseDate?: string;
}

export interface UpdateHoldingRequestDTO {
  quantity?: number;
  averageBuyPrice?: number;
  purchaseDate?: string;
}

export interface PredictionRequestDTO {
  ticker: string;
}

export interface PredictionResponseDTO {
  ticker: string;
  predictionLabel: 'UP' | 'DOWN';
  confidence: number;
  predictionForDate: string;
  modelVersion: number;
  createdAt?: string;
  validatedOn?: string | null;
  actualLabel?: 'UP' | 'DOWN' | null;
  correct?: boolean | null;
}

export interface PredictionAnalyticsResponseDTO {
  ticker: string;
  totalPredictions: number;
  correctPredictions: number;
  validatedPredictions: number;
  pendingPredictions: number;
  accuracy: number;
  averageConfidence: number;
}

export interface StockAnalyticsResponseDTO {
  ticker: string;
  currentPrice: number;
  totalReturn: number;
  averageDailyReturn: number;
  dailyVolatility: number;
  annualizedVolatility: number;
  sharpeRatio: number;
  maxDrawdown: number;
  bestDayReturn: number;
  worstDayReturn: number;
  dataPoints: number;
}

export interface PortfolioHoldingAnalyticsResponseDTO {
  ticker: string;
  quantity: number;
  averageBuyPrice: number;
  currentPrice: number;
  currentValue: number;
  profitLoss: number;
  returnPercentage: number;
  weight: number;
}

export interface PortfolioRiskAnalyticsResponseDTO {
  volatility: number;
  annualizedVolatility: number;
  sharpeRatio: number;
  maxDrawdown: number;
}

export interface PortfolioAnalyticsResponseDTO {
  portfolioId: ApiId;
  portfolioName: string;
  totalInvested: number;
  currentValue: number;
  totalProfitLoss: number;
  totalReturn: number;
  numberOfHoldings: number;
  holdings: PortfolioHoldingAnalyticsResponseDTO[];
  risk: PortfolioRiskAnalyticsResponseDTO;
}
