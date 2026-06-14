package com.anghel.investmenthelper.market.model.dto.fmp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialModelingPrepProfileDTO {

    private String symbol;

    private String companyName;

    private String currency;

    private String exchange;

    private String exchangeFullName;

    private String industry;

    private String sector;

    private BigDecimal marketCap;
}
