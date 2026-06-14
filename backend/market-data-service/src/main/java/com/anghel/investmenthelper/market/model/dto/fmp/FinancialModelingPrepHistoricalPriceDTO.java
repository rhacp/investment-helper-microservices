package com.anghel.investmenthelper.market.model.dto.fmp;

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
public class FinancialModelingPrepHistoricalPriceDTO {

    private String symbol;

    private LocalDate date;

    private BigDecimal open;

    private BigDecimal high;

    private BigDecimal low;

    private BigDecimal close;

    private Long volume;

    private BigDecimal change;

    private BigDecimal changePercent;

    private BigDecimal vwap;
}
