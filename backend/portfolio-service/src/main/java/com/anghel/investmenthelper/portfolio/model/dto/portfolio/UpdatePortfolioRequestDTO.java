package com.anghel.investmenthelper.portfolio.model.dto.portfolio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePortfolioRequestDTO {

    @NotBlank
    @Size(min = 1, max = 255, message = "Must be under 255 characters")
    private String name;
}
