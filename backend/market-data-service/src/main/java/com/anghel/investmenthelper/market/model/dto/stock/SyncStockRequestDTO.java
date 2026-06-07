package com.anghel.investmenthelper.market.model.dto.stock;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncStockRequestDTO {

    @NotBlank
    private String ticker;
}
