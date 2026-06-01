package com.anghel.investmenthelper.portfolio.model.dto.holding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoldingResponseDTO {

    private Long id;

    private String ticker;

    private BigDecimal quantity;

    private BigDecimal averageBuyPrice;

    private BigDecimal currentPrice;

    private BigDecimal profitLoss;

    private BigDecimal currentValue;

    private BigDecimal profitPercentage;
}
