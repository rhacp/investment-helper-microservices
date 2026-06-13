package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.model.entity.ModelTrainingRun;
import com.anghel.investmenthelper.prediction.model.entity.PredictionModelMetadata;

public interface ModelTrainingRunService {

    ModelTrainingRun createRun(String ticker);

    void markSuccess(Long runId, PredictionModelMetadata metadata, Integer recordsUsed, Double accuracy);

    void markFailed(Long runId, String errorMessage);
}
