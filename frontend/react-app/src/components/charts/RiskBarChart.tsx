import { Box, Typography } from '@mui/material';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { PortfolioRiskAnalyticsResponseDTO, StockAnalyticsResponseDTO } from '../../types/api';
import { formatNumber } from '../../utils/formatters';

export function RiskBarChart({ data }: { data?: PortfolioRiskAnalyticsResponseDTO | StockAnalyticsResponseDTO }) {
  if (!data) {
    return <Typography color="text.secondary">Run analytics to compare risk metrics.</Typography>;
  }

  const chartData = [
    { metric: 'Volatility', value: Number('volatility' in data ? data.volatility : data.dailyVolatility) },
    { metric: 'Annual Vol.', value: Number(data.annualizedVolatility) },
    { metric: 'Sharpe', value: Number(data.sharpeRatio) },
    { metric: 'Drawdown', value: Number(data.maxDrawdown) },
  ];

  return (
    <Box sx={{ width: '100%', height: 300 }}>
      <ResponsiveContainer>
        <BarChart data={chartData}>
          <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
          <XAxis dataKey="metric" stroke="#9CA3AF" />
          <YAxis stroke="#9CA3AF" />
          <Tooltip
            contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }}
            formatter={(value) => formatNumber(Number(value))}
          />
          <Bar dataKey="value" radius={[6, 6, 0, 0]} fill="#8B5CF6" />
        </BarChart>
      </ResponsiveContainer>
    </Box>
  );
}
