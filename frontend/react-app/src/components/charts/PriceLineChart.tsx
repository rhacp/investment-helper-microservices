import { Box, Typography } from '@mui/material';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import type { MarketPriceResponseDTO } from '../../types/api';
import { formatCurrency } from '../../utils/formatters';

export function PriceLineChart({ prices }: { prices: MarketPriceResponseDTO[] }) {
  const data = prices.map((price) => ({
    date: price.priceDate ?? price.date,
    close: Number(price.closePrice ?? 0),
  }));

  if (!data.length) {
    return <Typography color="text.secondary">No historical prices available.</Typography>;
  }

  return (
    <Box sx={{ width: '100%', height: 320 }}>
      <ResponsiveContainer>
        <AreaChart data={data}>
          <defs>
            <linearGradient id="priceLine" x1="0" x2="0" y1="0" y2="1">
              <stop offset="5%" stopColor="#7C3AED" stopOpacity={0.45} />
              <stop offset="95%" stopColor="#7C3AED" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid stroke="rgba(148, 163, 184, 0.14)" vertical={false} />
          <XAxis dataKey="date" stroke="#9CA3AF" minTickGap={30} />
          <YAxis stroke="#9CA3AF" domain={['auto', 'auto']} tickFormatter={(value) => `$${value}`} />
          <Tooltip
            contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }}
            formatter={(value) => formatCurrency(Number(value))}
          />
          <Area type="monotone" dataKey="close" stroke="#8B5CF6" strokeWidth={2} fill="url(#priceLine)" />
        </AreaChart>
      </ResponsiveContainer>
    </Box>
  );
}
