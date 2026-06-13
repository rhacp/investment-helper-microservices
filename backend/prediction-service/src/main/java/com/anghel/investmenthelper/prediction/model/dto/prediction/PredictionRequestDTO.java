package com.anghel.investmenthelper.prediction.model.dto.prediction;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PredictionRequestDTO {

    @NotBlank
    private String ticker;
}
