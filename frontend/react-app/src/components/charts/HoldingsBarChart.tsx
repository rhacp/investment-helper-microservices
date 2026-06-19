import { Box, Typography } from '@mui/material';
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { HoldingResponseDTO, PortfolioHoldingAnalyticsResponseDTO } from '../../types/api';
import { formatCurrency } from '../../utils/formatters';

type HoldingChartItem = HoldingResponseDTO | PortfolioHoldingAnalyticsResponseDTO;

export function HoldingsBarChart({
  holdings,
  metric,
}: {
  holdings: HoldingChartItem[];
  metric: 'currentValue' | 'profitLoss';
}) {
  const data = holdings.map((holding) => ({ ticker: holding.ticker, value: Number(holding[metric] ?? 0) }));

  if (!data.length) {
    return <Typography color="text.secondary">No holdings to chart yet.</Typography>;
  }

  return (
    <Box sx={{ width: '100%', height: 300 }}>
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
          <XAxis dataKey="ticker" stroke="#9CA3AF" />
          <YAxis stroke="#9CA3AF" tickFormatter={(value) => `$${Number(value) / 1000}k`} />
          <Tooltip
            contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }}
            formatter={(value) => formatCurrency(Number(value))}
          />
          <Bar dataKey="value" radius={[6, 6, 0, 0]} fill={metric === 'profitLoss' ? '#22C55E' : '#7C3AED'} />
        </BarChart>
      </ResponsiveContainer>
    </Box>
  );
}
