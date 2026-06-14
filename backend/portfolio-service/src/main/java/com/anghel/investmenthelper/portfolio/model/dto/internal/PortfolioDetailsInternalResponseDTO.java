package com.anghel.investmenthelper.portfolio.model.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDetailsInternalResponseDTO {

    private Long id;

    private String name;

    private Long authUserId;

    private List<HoldingInternalResponseDTO> holdings;
}
