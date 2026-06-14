package com.anghel.investmenthelper.portfolio.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HoldingInternalResponseDTO {

    private String ticker;

    private BigDecimal quantity;

    private BigDecimal averageBuyPrice;
}
