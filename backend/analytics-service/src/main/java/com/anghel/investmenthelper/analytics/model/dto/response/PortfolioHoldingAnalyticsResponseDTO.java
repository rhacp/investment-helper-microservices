package com.anghel.investmenthelper.analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioHoldingAnalyticsResponseDTO {

    private String ticker;

    private BigDecimal quantity;

    private BigDecimal averageBuyPrice;

    private BigDecimal currentPrice;

    private BigDecimal currentValue;

    private BigDecimal profitLoss;

    private BigDecimal returnPercentage;

    private BigDecimal weight;
}
