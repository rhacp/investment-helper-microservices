package com.anghel.investmenthelper.analytics.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarketPriceInternalResponseDTO {

    private String ticker;

    private BigDecimal price;
}
