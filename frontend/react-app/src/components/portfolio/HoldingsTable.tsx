import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import {
  Chip,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from '@mui/material';
import type { HoldingResponseDTO } from '../../types/api';
import { formatCurrency, formatNumber, formatRatioPercent, getSignedColor } from '../../utils/formatters';

interface HoldingsTableProps {
  holdings: HoldingResponseDTO[];
  onEdit: (holding: HoldingResponseDTO) => void;
  onDelete: (holding: HoldingResponseDTO) => void;
}

export function HoldingsTable({ holdings, onEdit, onDelete }: HoldingsTableProps) {
  return (
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Ticker</TableCell>
            <TableCell align="right">Quantity</TableCell>
            <TableCell align="right">Average buy</TableCell>
            <TableCell align="right">Current price</TableCell>
            <TableCell align="right">Current value</TableCell>
            <TableCell align="right">P/L</TableCell>
            <TableCell align="right">Return</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {holdings.map((holding) => (
            <TableRow key={holding.id} hover>
              <TableCell>
                <Chip label={holding.ticker} color="primary" variant="outlined" />
              </TableCell>
              <TableCell align="right">{formatNumber(holding.quantity)}</TableCell>
              <TableCell align="right">{formatCurrency(holding.averageBuyPrice)}</TableCell>
              <TableCell align="right">{formatCurrency(holding.currentPrice)}</TableCell>
              <TableCell align="right">{formatCurrency(holding.currentValue)}</TableCell>
              <TableCell align="right" sx={{ color: getSignedColor(holding.profitLoss), fontWeight: 800 }}>
                {formatCurrency(holding.profitLoss)}
              </TableCell>
              <TableCell align="right" sx={{ color: getSignedColor(holding.profitPercentage), fontWeight: 800 }}>
                {formatRatioPercent(holding.profitPercentage)}
              </TableCell>
              <TableCell align="right">
                <Tooltip title="Edit holding">
                  <IconButton onClick={() => onEdit(holding)} aria-label="edit holding">
                    <EditOutlinedIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete holding">
                  <IconButton color="error" onClick={() => onDelete(holding)} aria-label="delete holding">
                    <DeleteOutlineIcon />
                  </IconButton>
                </Tooltip>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
