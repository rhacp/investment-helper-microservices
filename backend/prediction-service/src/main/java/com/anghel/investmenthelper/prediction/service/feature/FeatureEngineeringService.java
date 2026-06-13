package com.anghel.investmenthelper.prediction.service.feature;

import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.prediction.model.internal.PredictionRow;
import com.anghel.investmenthelper.prediction.model.internal.TrainingRow;

import java.util.List;

public interface FeatureEngineeringService {

    List<TrainingRow> buildTrainingRows(List<MarketPriceResponseDTO> marketPriceList);

    PredictionRow buildPredictionRow(List<MarketPriceResponseDTO> marketPriceList);
}
