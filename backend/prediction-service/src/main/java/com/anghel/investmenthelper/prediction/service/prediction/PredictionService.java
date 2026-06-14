package com.anghel.investmenthelper.prediction.service.prediction;

import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionAnalyticsResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionResponseDTO;

public interface PredictionService {

    PredictionResponseDTO predict(PredictionRequestDTO request);

    PredictionResponseDTO getLatestPrediction(String ticker);

    PredictionAnalyticsResponseDTO getAnalytics(String ticker);
}
