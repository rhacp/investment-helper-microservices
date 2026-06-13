package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.model.dto.TrainingModelRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.TrainingModelResponseDTO;

public interface PredictionModelMetadataService {

    TrainingModelResponseDTO trainModel(TrainingModelRequestDTO request);
}
