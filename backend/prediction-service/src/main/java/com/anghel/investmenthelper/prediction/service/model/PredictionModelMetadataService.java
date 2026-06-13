package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.model.dto.training.TrainingModelRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.training.TrainingModelResponseDTO;

public interface PredictionModelMetadataService {

    TrainingModelResponseDTO trainModel(TrainingModelRequestDTO request);
}
