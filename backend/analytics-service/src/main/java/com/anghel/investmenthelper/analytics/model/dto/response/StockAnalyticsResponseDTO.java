package com.anghel.investmenthelper.analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockAnalyticsResponseDTO {

    private String ticker;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private BigDecimal currentPrice;

    private BigDecimal startPrice;

    private BigDecimal totalReturn;

    private BigDecimal averageDailyReturn;

    private BigDecimal volatility;

    private BigDecimal annualizedVolatility;

    private BigDecimal maxDrawdown;

    private BigDecimal sharpeRatio;

    private BigDecimal bestDayReturn;

    private BigDecimal worstDayReturn;

    private int numberOfPrices;
}
