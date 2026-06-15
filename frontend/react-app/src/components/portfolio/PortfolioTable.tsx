import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import EditOutlinedIcon from '@mui/icons-material/EditOutlined';
import VisibilityOutlinedIcon from '@mui/icons-material/VisibilityOutlined';
import {
  Chip,
  IconButton,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
} from '@mui/material';
import type { PortfolioResponseDTO } from '../../types/api';
import { formatCurrency, getSignedColor } from '../../utils/formatters';

interface PortfolioTableProps {
  portfolios: PortfolioResponseDTO[];
  onView: (portfolio: PortfolioResponseDTO) => void;
  onEdit: (portfolio: PortfolioResponseDTO) => void;
  onDelete: (portfolio: PortfolioResponseDTO) => void;
}

export function PortfolioTable({ portfolios, onView, onEdit, onDelete }: PortfolioTableProps) {
  return (
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell align="right">Total value</TableCell>
            <TableCell align="right">Profit / loss</TableCell>
            <TableCell align="right">Holdings</TableCell>
            <TableCell align="right">Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {portfolios.map((portfolio) => (
            <TableRow key={portfolio.id} hover>
              <TableCell>
                <Stack direction="row" spacing={1} alignItems="center">
                  <strong>{portfolio.name}</strong>
                  <Chip size="small" label={`#${portfolio.id}`} />
                </Stack>
              </TableCell>
              <TableCell align="right">{formatCurrency(portfolio.totalValue)}</TableCell>
              <TableCell align="right" sx={{ color: getSignedColor(portfolio.totalProfitLoss), fontWeight: 800 }}>
                {formatCurrency(portfolio.totalProfitLoss)}
              </TableCell>
              <TableCell align="right">{portfolio.holdings?.length ?? 0}</TableCell>
              <TableCell align="right">
                <Tooltip title="Open portfolio">
                  <IconButton onClick={() => onView(portfolio)} aria-label="open portfolio">
                    <VisibilityOutlinedIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Edit portfolio">
                  <IconButton onClick={() => onEdit(portfolio)} aria-label="edit portfolio">
                    <EditOutlinedIcon />
                  </IconButton>
                </Tooltip>
                <Tooltip title="Delete portfolio">
                  <IconButton color="error" onClick={() => onDelete(portfolio)} aria-label="delete portfolio">
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
