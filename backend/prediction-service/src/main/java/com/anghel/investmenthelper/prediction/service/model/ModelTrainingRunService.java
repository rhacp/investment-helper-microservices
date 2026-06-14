package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.model.entity.ModelTrainingRun;

public interface ModelTrainingRunService {

    ModelTrainingRun createRun(String ticker);

    void markSuccess(Long runId, Long metadataId, Integer recordsUsed, Double accuracy);

    void markFailed(Long runId, String errorMessage);
}
