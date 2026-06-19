package com.anghel.investmenthelper.prediction.service.prediction;

import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionAnalyticsResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.ValidatedPredictionResponseDTO;

import java.util.List;

public interface PredictionService {

    PredictionResponseDTO getLatestPrediction(String ticker);

    PredictionAnalyticsResponseDTO getAnalytics(String ticker);

    PredictionResponseDTO generatePrediction(String ticker);

    List<ValidatedPredictionResponseDTO> getLatestDayPredictions();
}
