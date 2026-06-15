package com.anghel.investmenthelper.prediction.service.prediction;

import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionAnalyticsResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionResponseDTO;

public interface PredictionService {

    PredictionResponseDTO getLatestPrediction(String ticker);

    PredictionAnalyticsResponseDTO getAnalytics(String ticker);

    PredictionResponseDTO generatePrediction(String ticker);
}
