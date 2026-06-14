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
public class PortfolioRiskAnalyticsResponseDTO {

    private BigDecimal volatility;

    private BigDecimal annualizedVolatility;

    private BigDecimal sharpeRatio;

    private BigDecimal maxDrawdown;
}
