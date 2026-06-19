package com.anghel.investmenthelper.prediction.model.dto.prediction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PredictionAnalyticsResponseDTO {

    private String ticker;

    private Integer totalPredictions;

    private Integer correctPredictions;

    private Integer validatedPredictions;

    private Integer pendingPredictions;

    private Double accuracy;

    private Double averageConfidence;
}
