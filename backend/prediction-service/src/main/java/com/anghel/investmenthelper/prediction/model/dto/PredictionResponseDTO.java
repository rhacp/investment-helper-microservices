package com.anghel.investmenthelper.prediction.model.dto;

import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponseDTO {

    private String ticker;

    private PredictionLabel predictionLabel;

    private Double confidence;

    private Integer modelVersion;

    private LocalDate predictionForDate;
}
