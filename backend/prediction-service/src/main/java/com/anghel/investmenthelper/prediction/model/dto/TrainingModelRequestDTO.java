package com.anghel.investmenthelper.prediction.model.dto;

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
public class TrainingModelRequestDTO {

    @NotBlank
    @Size(min = 1, max = 10)
    private String ticker;
}
