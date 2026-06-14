package com.anghel.investmenthelper.analytics.service.stock;

import com.anghel.investmenthelper.analytics.client.MarketDataClient;
import com.anghel.investmenthelper.analytics.model.dto.internal.MarketPriceResponseDTO;
import com.anghel.investmenthelper.analytics.model.dto.response.StockAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.service.risk.RiskMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockAnalyticsServiceImpl implements StockAnalyticsService {

    private final MarketDataClient marketDataClient;

    private final RiskMetricsService riskMetricsService;

    @Override
    public StockAnalyticsResponseDTO getStockAnalytics(String ticker) {
        log.info("Generating analytics for stock {}", ticker);
        List<MarketPriceResponseDTO> prices = marketDataClient.getHistoricalPrices(ticker);

        log.debug("Retrieved {} historical prices for {}", prices.size(), ticker);

        List<BigDecimal> closingPrices = prices.stream()
                .map(MarketPriceResponseDTO::getClosePrice)
                .toList();

        List<BigDecimal> dailyReturns = riskMetricsService.calculateDailyReturns(closingPrices);
        BigDecimal totalReturn = riskMetricsService.calculateTotalReturn(closingPrices);
        BigDecimal averageReturn = riskMetricsService.calculateAverageReturn(dailyReturns);
        BigDecimal volatility = riskMetricsService.calculateVolatility(dailyReturns);
        BigDecimal annualizedVolatility = riskMetricsService.calculateAnnualizedVolatility(volatility);
        BigDecimal sharpeRatio = riskMetricsService.calculateSharpeRatio(averageReturn, volatility);
        BigDecimal maxDrawdown = riskMetricsService.calculateMaxDrawdown(closingPrices);
        BigDecimal bestDay = riskMetricsService.findBestDayReturn(dailyReturns);
        BigDecimal worstDay = riskMetricsService.findWorstDayReturn(dailyReturns);
        BigDecimal currentPrice = closingPrices.getLast();
        BigDecimal startPrice = closingPrices.getFirst();

        log.info("Analytics generated successfully for {}", ticker);

        return new StockAnalyticsResponseDTO(
                ticker,
                prices.getFirst().getDate(),
                prices.getLast().getDate(),
                currentPrice,
                startPrice,
                totalReturn,
                averageReturn,
                volatility,
                annualizedVolatility,
                maxDrawdown,
                sharpeRatio,
                bestDay,
                worstDay,
                prices.size()
        );
    }
}
