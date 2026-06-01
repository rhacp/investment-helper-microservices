package com.anghel.investmenthelper.portfolio.model.dto;

import com.anghel.investmenthelper.portfolio.model.entity.Holding;
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
public class PortfolioResponseDTO {

    private Long id;

    private String name;

    private BigDecimal totalValue;

    private BigDecimal totalProfitLoss;

    private List<Holding> holdings;
}
