package com.anghel.investmenthelper.market.model.dto;

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

    String ticker;

    BigDecimal price;
}
