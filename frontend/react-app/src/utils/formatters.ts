export const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
});

export const numberFormatter = new Intl.NumberFormat('en-US', {
  maximumFractionDigits: 2,
});

export const compactNumberFormatter = new Intl.NumberFormat('en-US', {
  notation: 'compact',
  maximumFractionDigits: 2,
});

export function formatCurrency(value?: number | null) {
  return currencyFormatter.format(Number(value ?? 0));
}

export function formatNumber(value?: number | null) {
  return numberFormatter.format(Number(value ?? 0));
}

export function formatPercent(value?: number | null) {
  return `${numberFormatter.format(Number(value ?? 0))}%`;
}

export function formatRatioPercent(value?: number | null) {
  return `${numberFormatter.format(Number(value ?? 0) * 100)}%`;
}

export function getSignedColor(value?: number | null) {
  const numeric = Number(value ?? 0);
  if (numeric > 0) return 'success.main';
  if (numeric < 0) return 'error.main';
  return 'text.secondary';
}

export function normalizeTicker(ticker: string) {
  return ticker.trim().toUpperCase();
}
