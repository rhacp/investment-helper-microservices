package com.anghel.investmenthelper.analytics.service.risk;

import java.math.BigDecimal;
import java.util.List;

public interface RiskMetricsService {

    List<BigDecimal> calculateDailyReturns(List<BigDecimal> closingPrices);

    BigDecimal calculateTotalReturn(List<BigDecimal> closingPrices);

    BigDecimal calculateAverageReturn(List<BigDecimal> returns);

    BigDecimal calculateVolatility(List<BigDecimal> returns);

    BigDecimal calculateAnnualizedVolatility(BigDecimal dailyVolatility);

    BigDecimal calculateSharpeRatio(BigDecimal averageReturn, BigDecimal volatility);

    BigDecimal calculateMaxDrawdown(List<BigDecimal> closingPrices);

    BigDecimal findBestDayReturn(List<BigDecimal> returns);

    BigDecimal findWorstDayReturn(List<BigDecimal> returns);
}
