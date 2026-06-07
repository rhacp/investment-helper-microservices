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
public class StockResponseDTO {

    private String ticker;

    private String companyName;

    private String currency;

    private String exchange;

    private BigDecimal currentPrice;
}
