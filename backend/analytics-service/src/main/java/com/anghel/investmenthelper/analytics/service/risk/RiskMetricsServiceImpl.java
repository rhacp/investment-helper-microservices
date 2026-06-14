package com.anghel.investmenthelper.analytics.service.risk;

import com.anghel.investmenthelper.analytics.exception.AnalyticsCalculationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RiskMetricsServiceImpl implements RiskMetricsService {

    private static final int SCALE = 8;

    private static final int TRADING_DAYS_PER_YEAR = 252;

    @Override
    public List<BigDecimal> calculateDailyReturns(List<BigDecimal> closingPrices) {
        validateClosingPrices(closingPrices);

        log.debug("Calculating daily returns for {} prices", closingPrices.size());
        List<BigDecimal> returns = new ArrayList<>();

        for (int i = 1; i < closingPrices.size(); i++) {
            BigDecimal previous = closingPrices.get(i - 1);
            BigDecimal current = closingPrices.get(i);

            if (previous.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("Skipping daily return calculation because previous price is zero at index {}",
                        i - 1);
                continue;
            }

            BigDecimal dailyReturn = current.subtract(previous).divide(previous, SCALE, RoundingMode.HALF_UP);
            returns.add(dailyReturn);
        }

        log.debug("Calculated {} daily returns", returns.size());
        return returns;
    }

    @Override
    public BigDecimal calculateTotalReturn(List<BigDecimal> closingPrices) {
        validateClosingPrices(closingPrices);

        BigDecimal first = closingPrices.getFirst();
        BigDecimal last = closingPrices.getLast();

        if (first.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Cannot calculate total return because initial price is zero");
            throw new AnalyticsCalculationException("Cannot calculate return because initial price is zero");
        }

        BigDecimal result = last.subtract(first).divide(first, SCALE, RoundingMode.HALF_UP);

        log.debug("Total return calculated: {}", result);
        return result;
    }

    @Override
    public BigDecimal calculateAverageReturn(List<BigDecimal> returns) {
        validateReturns(returns);

        BigDecimal sum = returns.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal result = sum.divide(BigDecimal.valueOf(returns.size()), SCALE, RoundingMode.HALF_UP);

        log.debug("Average return calculated: {}", result);
        return result;
    }

    @Override
    public BigDecimal calculateVolatility(List<BigDecimal> returns) {
        validateReturnsForVariance(returns);

        BigDecimal mean = calculateAverageReturn(returns);

        double squaredDifferencesSum = returns.stream()
                .mapToDouble(returnValue -> Math.pow(returnValue.subtract(mean).doubleValue(), 2))
                .sum();

        double variance = squaredDifferencesSum / (returns.size() - 1);
        BigDecimal volatility = BigDecimal.valueOf(Math.sqrt(variance));

        log.debug("Volatility calculated: {}", volatility);
        return volatility;
    }

    @Override
    public BigDecimal calculateAnnualizedVolatility(BigDecimal dailyVolatility) {
        if (dailyVolatility == null) {
            log.warn("Cannot calculate annualized volatility because daily volatility is null");
            throw new AnalyticsCalculationException("Daily volatility cannot be null");
        }

        BigDecimal result = BigDecimal.valueOf(dailyVolatility.doubleValue() * Math.sqrt(TRADING_DAYS_PER_YEAR));

        log.debug("Annualized volatility calculated: {}", result);
        return result;
    }

    @Override
    public BigDecimal calculateSharpeRatio(BigDecimal averageReturn, BigDecimal volatility) {
        if (averageReturn == null) {
            log.warn("Cannot calculate Sharpe ratio because average return is null");
            throw new AnalyticsCalculationException("Average return cannot be null");
        }

        if (volatility == null || volatility.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal result = BigDecimal.valueOf((averageReturn.doubleValue() / volatility.doubleValue())
                * Math.sqrt(TRADING_DAYS_PER_YEAR));

        log.debug("Sharpe ratio calculated: {}", result);
        return result;
    }

    @Override
    public BigDecimal calculateMaxDrawdown(List<BigDecimal> closingPrices) {
        validateClosingPrices(closingPrices);

        BigDecimal peak = closingPrices.getFirst();
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        if (peak.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid price data detected. Peak price={}", peak);
            throw new AnalyticsCalculationException("Invalid price data detected");
        }

        for (BigDecimal price : closingPrices) {
            if (price.compareTo(peak) > 0) {
                peak = price;
            }

            BigDecimal drawdown = price.subtract(peak).divide(peak, SCALE, RoundingMode.HALF_UP);

            if (drawdown.compareTo(maxDrawdown) < 0) {
                maxDrawdown = drawdown;
            }
        }

        log.debug("Max drawdown calculated: {}", maxDrawdown);
        return maxDrawdown;
    }

    @Override
    public BigDecimal findBestDayReturn(List<BigDecimal> returns) {
        validateReturns(returns);

        BigDecimal result = returns.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        log.debug("Best day return: {}", result);
        return result;
    }

    @Override
    public BigDecimal findWorstDayReturn(List<BigDecimal> returns) {
        validateReturns(returns);

        BigDecimal result = returns.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        log.debug("Worst day return: {}", result);
        return result;
    }

    private void validateReturns(List<BigDecimal> returns) {
        if (returns == null || returns.isEmpty()) {
            log.warn("No returns available for analytics calculation. Count={}",
                    returns == null ? 0 : returns.size());

            throw new AnalyticsCalculationException("No returns available");
        }
    }

    private void validateClosingPrices(List<BigDecimal> closingPrices) {
        if (closingPrices == null || closingPrices.size() < 2) {
            log.warn("Insufficient closing prices provided. Count={}",
                    closingPrices == null ? 0 : closingPrices.size());

            throw new AnalyticsCalculationException("At least 2 closing prices are required");
        }
    }

    private void validateReturnsForVariance(List<BigDecimal> returns) {
        validateReturns(returns);

        if (returns.size() < 2) {
            log.warn("At least 2 returns are required to calculate variance. Count={}", returns.size());
            throw new AnalyticsCalculationException("At least 2 returns are required to calculate volatility");
        }
    }
}
