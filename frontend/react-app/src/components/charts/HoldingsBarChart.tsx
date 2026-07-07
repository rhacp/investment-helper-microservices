import { Box, Typography } from '@mui/material';
import { Bar, BarChart, CartesianGrid, Cell, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
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
  const data = holdings.map((holding, index) => ({
    id: `${holding.ticker}-${index}`,
    ticker: holding.ticker,
    value: Number(holding[metric] ?? 0),
  }));
  const isProfitLossChart = metric === 'profitLoss';

  if (!data.length) {
    return <Typography color="text.secondary">No holdings to chart yet.</Typography>;
  }

  return (
    <Box sx={{ width: '100%', height: 300 }}>
      <ResponsiveContainer>
        <BarChart data={data}>
          <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
          <XAxis dataKey="id" stroke="#9CA3AF" tickFormatter={(_value, index) => data[index]?.ticker ?? ''} />
          <YAxis stroke="#9CA3AF" tickFormatter={(value) => `$${Number(value) / 1000}k`} />
          <Tooltip
            contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }}
            content={({ active, payload }) => {
              if (!active || !payload?.length) {
                return null;
              }

              const activePoint = payload[0]?.payload;
              const actualValue = Number(activePoint?.value ?? 0);
              const valueColor = isProfitLossChart ? (actualValue < 0 ? '#EF4444' : '#22C55E') : '#E9D5FF';

              return (
                <Box sx={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)', px: 1.5, py: 1 }}>
                  <Typography sx={{ color: '#FFFFFF', fontWeight: 600, mb: 0.5 }}>{activePoint?.ticker ?? ''}</Typography>
                  <Typography sx={{ color: valueColor }}>value: {formatCurrency(actualValue)}</Typography>
                </Box>
              );
            }}
          />
          <Bar dataKey="value" radius={[6, 6, 0, 0]} fill={isProfitLossChart ? '#22C55E' : '#7C3AED'}>
            {isProfitLossChart &&
              data.map((entry) => (
                <Cell
                  key={entry.id}
                  fill={entry.value < 0 ? '#EF4444' : '#22C55E'}
                />
              ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </Box>
  );
}
