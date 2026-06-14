package com.anghel.investmenthelper.analytics.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioAnalyticsResponseDTO {

    private Long portfolioId;

    private String portfolioName;

    private BigDecimal totalInvested;

    private BigDecimal currentValue;

    private BigDecimal totalProfitLoss;

    private BigDecimal totalReturn;

    private int numberOfHoldings;

    private List<PortfolioHoldingAnalyticsResponseDTO> holdings;

    private PortfolioRiskAnalyticsResponseDTO risk;
}
