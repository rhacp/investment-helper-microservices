import { Box, Typography } from '@mui/material';
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from 'recharts';
import type { HoldingResponseDTO, PortfolioHoldingAnalyticsResponseDTO } from '../../types/api';
import { formatCurrency, formatPercent } from '../../utils/formatters';

const COLORS = ['#7C3AED', '#8B5CF6', '#22C55E', '#F59E0B', '#EF4444', '#C084FC'];

type AllocationItem = HoldingResponseDTO | PortfolioHoldingAnalyticsResponseDTO;
type TooltipPayload = { payload?: { name?: string; weight?: number } };

export function AllocationPieChart({ holdings }: { holdings: AllocationItem[] }) {
  const total = holdings.reduce((sum, item) => sum + Number(item.currentValue ?? 0), 0);
  const data = holdings.map((item) => ({
    name: item.ticker,
    value: Number(item.currentValue ?? 0),
    weight: 'weight' in item ? Number(item.weight ?? 0) * 100 : total ? (Number(item.currentValue ?? 0) / total) * 100 : 0,
  }));

  if (!data.length) {
    return <Typography color="text.secondary">No holdings to chart yet.</Typography>;
  }

  return (
    <Box sx={{ width: '100%', height: 300 }}>
      <ResponsiveContainer>
        <PieChart>
          <Pie data={data} dataKey="value" nameKey="name" innerRadius={70} outerRadius={105} paddingAngle={2}>
            {data.map((entry, index) => (
              <Cell key={entry.name} fill={COLORS[index % COLORS.length]} />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{ background: '#151028', border: '1px solid rgba(196, 181, 253, 0.22)' }}
            formatter={(value, _name, item) => {
              const payload = (item as TooltipPayload).payload;
              return [formatCurrency(Number(value)), `${payload?.name ?? ''} ${formatPercent(payload?.weight)}`];
            }}
          />
        </PieChart>
      </ResponsiveContainer>
    </Box>
  );
}
