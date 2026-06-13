package com.anghel.investmenthelper.prediction.model.dto.training;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingModelResponseDTO {

    private String ticker;

    private LocalDateTime trainedAt;

    private Integer recordsUsed;

    private Double accuracy;
}
