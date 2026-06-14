package com.anghel.investmenthelper.analytics.service.portfolio;

import com.anghel.investmenthelper.analytics.client.MarketDataClient;
import com.anghel.investmenthelper.analytics.client.PortfolioClient;
import com.anghel.investmenthelper.analytics.exception.AnalyticsCalculationException;
import com.anghel.investmenthelper.analytics.model.dto.internal.*;
import com.anghel.investmenthelper.analytics.model.dto.response.PortfolioAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.model.dto.response.PortfolioHoldingAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.model.dto.response.PortfolioRiskAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.service.risk.RiskMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioAnalyticsServiceImpl implements PortfolioAnalyticsService {

    private final PortfolioClient portfolioClient;

    private final MarketDataClient marketDataClient;

    private final RiskMetricsService riskMetricsService;

    @Override
    public PortfolioAnalyticsResponseDTO getPortfolioAnalytics(Long portfolioId) {
        log.info("Generating analytics for portfolio [portfolioId={}]", portfolioId);
        PortfolioDetailsInternalResponseDTO portfolio = portfolioClient.getPortfolioDetails(portfolioId);
        log.debug("Retrieved portfolio details [portfolioId={}, holdings={}]",
                portfolioId,
                portfolio.getHoldings().size());

        Map<String, MarketPriceInternalResponseDTO> latestPrices = getLatestPrices(portfolio);
        List<PortfolioHoldingAnalyticsResponseDTO> holdingAnalytics = buildHoldingAnalytics(portfolio, latestPrices);

        BigDecimal totalInvested = calculateTotalInvested(holdingAnalytics);
        BigDecimal currentValue = calculateCurrentValue(holdingAnalytics);
        BigDecimal totalProfitLoss = currentValue.subtract(totalInvested);
        BigDecimal totalReturn = calculateReturnPercentage(totalInvested, currentValue);
        PortfolioRiskAnalyticsResponseDTO risk = calculatePortfolioRisk(portfolio);

        log.info("Analytics generated successfully for portfolio [portfolioId={}]", portfolioId);

        return new PortfolioAnalyticsResponseDTO(
                portfolio.getId(),
                portfolio.getName(),
                totalInvested,
                currentValue,
                totalProfitLoss,
                totalReturn,
                holdingAnalytics.size(),
                holdingAnalytics,
                risk
        );
    }

    private PortfolioHoldingAnalyticsResponseDTO buildHoldingAnalytics(HoldingInternalResponseDTO holding,
                                                                       Map<String, MarketPriceInternalResponseDTO> latestPrices) {
        MarketPriceInternalResponseDTO marketPrice = latestPrices.get(holding.getTicker());
        log.debug("Processing holding [ticker={}]", holding.getTicker());

        if (marketPrice == null) {
            log.warn("Market price not found for ticker [ticker={}]", holding.getTicker());
            throw new AnalyticsCalculationException("Market price not found for ticker " + holding.getTicker());
        }

        BigDecimal currentPrice = marketPrice.getPrice();
        BigDecimal investedAmount = holding.getQuantity().multiply(holding.getAverageBuyPrice());
        BigDecimal currentValue = holding.getQuantity().multiply(currentPrice);
        BigDecimal profitLoss = currentValue.subtract(investedAmount);
        BigDecimal returnPercentage = calculateReturnPercentage(investedAmount, currentValue);

        return new PortfolioHoldingAnalyticsResponseDTO(
                holding.getTicker(),
                holding.getQuantity(),
                holding.getAverageBuyPrice(),
                currentPrice,
                currentValue,
                profitLoss,
                returnPercentage,
                BigDecimal.ZERO
        );
    }

    private List<PortfolioHoldingAnalyticsResponseDTO> buildHoldingAnalytics(PortfolioDetailsInternalResponseDTO portfolio,
                                                                             Map<String, MarketPriceInternalResponseDTO> latestPrices) {
        List<PortfolioHoldingAnalyticsResponseDTO> holdings = portfolio.getHoldings().stream()
                .map(holding -> buildHoldingAnalytics(holding, latestPrices))
                .toList();

        BigDecimal portfolioValue = holdings.stream()
                .map(PortfolioHoldingAnalyticsResponseDTO::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        holdings.forEach(holding -> holding.setWeight(calculateWeight(holding.getCurrentValue(), portfolioValue)));
        return holdings;
    }

    private PortfolioRiskAnalyticsResponseDTO calculatePortfolioRisk(PortfolioDetailsInternalResponseDTO portfolio) {
        if (portfolio.getHoldings().isEmpty()) {
            log.warn("Portfolio contains no holdings [portfolioId={}]", portfolio.getId());
            return new PortfolioRiskAnalyticsResponseDTO(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        List<BigDecimal> portfolioReturns = new ArrayList<>();
        BigDecimal maxDrawdown = BigDecimal.ZERO;

        for (HoldingInternalResponseDTO holding : portfolio.getHoldings()) {
            log.debug("Calculating analytics for holding [ticker={}]", holding.getTicker());
            List<BigDecimal> closes = marketDataClient.getHistoricalPrices(holding.getTicker()).stream()
                    .map(MarketPriceResponseDTO::getClosePrice)
                    .toList();

            portfolioReturns.addAll(riskMetricsService.calculateDailyReturns(closes));
            BigDecimal holdingDrawdown = riskMetricsService.calculateMaxDrawdown(closes);
            if (holdingDrawdown.compareTo(maxDrawdown) < 0) {
                maxDrawdown = holdingDrawdown;
            }
        }

        BigDecimal averageReturn = riskMetricsService.calculateAverageReturn(portfolioReturns);
        BigDecimal volatility = riskMetricsService.calculateVolatility(portfolioReturns);
        BigDecimal annualizedVolatility = riskMetricsService.calculateAnnualizedVolatility(volatility);
        BigDecimal sharpeRatio = riskMetricsService.calculateSharpeRatio(averageReturn, volatility);

        return new PortfolioRiskAnalyticsResponseDTO(
                volatility,
                annualizedVolatility,
                sharpeRatio,
                maxDrawdown
        );
    }

    private BigDecimal calculateTotalInvested(List<PortfolioHoldingAnalyticsResponseDTO> holdings) {

        return holdings.stream()
                .map(holding -> holding.getQuantity().multiply(holding.getAverageBuyPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateCurrentValue(List<PortfolioHoldingAnalyticsResponseDTO> holdings) {
        return holdings.stream()
                .map(PortfolioHoldingAnalyticsResponseDTO::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateReturnPercentage(BigDecimal invested, BigDecimal current) {
        if (invested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return current.subtract(invested).divide(invested, 8, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateWeight(BigDecimal holdingValue, BigDecimal portfolioValue) {
        if (portfolioValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return holdingValue.divide(portfolioValue, 8, RoundingMode.HALF_UP);
    }

    private Map<String, MarketPriceInternalResponseDTO> getLatestPrices(PortfolioDetailsInternalResponseDTO portfolio) {
        List<String> tickers = portfolio.getHoldings().stream()
                .map(HoldingInternalResponseDTO::getTicker)
                .distinct()
                .toList();

        log.debug("Retrieving latest prices for {} tickers", tickers.size());

        return marketDataClient.getLatestPrices(new BatchMarketPriceRequestDTO(tickers));
    }
}
