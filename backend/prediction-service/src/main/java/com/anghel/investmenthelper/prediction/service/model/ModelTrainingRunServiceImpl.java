package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.model.entity.ModelTrainingRun;
import com.anghel.investmenthelper.prediction.model.entity.PredictionModelMetadata;
import com.anghel.investmenthelper.prediction.repository.ModelTrainingRunRepository;
import com.anghel.investmenthelper.prediction.repository.PredictionModelMetadataRepository;
import com.anghel.investmenthelper.prediction.util.enumeration.TrainingStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ModelTrainingRunServiceImpl implements ModelTrainingRunService {

    private final PredictionModelMetadataRepository predictionModelMetadataRepository;

    private final ModelTrainingRunRepository repository;

    public ModelTrainingRunServiceImpl(PredictionModelMetadataRepository predictionModelMetadataRepository, ModelTrainingRunRepository repository) {
        this.predictionModelMetadataRepository = predictionModelMetadataRepository;
        this.repository = repository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ModelTrainingRun createRun(String ticker) {

        ModelTrainingRun run = new ModelTrainingRun();
        run.setTicker(ticker);
        run.setStartedAt(LocalDateTime.now());
        ModelTrainingRun savedRun = repository.save(run);

        log.info(
                "Training run created [runId={}, ticker={}]",
                savedRun.getId(),
                ticker
        );

        return savedRun;
    }

    @Override
    @Transactional
    public void markSuccess(Long runId, Long metadataId, Integer recordsUsed, Double accuracy) {
        ModelTrainingRun run = repository.findById(runId)
                .orElseThrow(() -> new RuntimeException("Run not found: " + runId));

        PredictionModelMetadata metadata = predictionModelMetadataRepository.findById(metadataId)
                        .orElseThrow(() -> new RuntimeException("Metadata not found: " + metadataId));

        run.setFinishedAt(LocalDateTime.now());
        run.setStatus(TrainingStatus.SUCCESS);
        run.setPredictionModelMetadata(metadata);
        run.setRecordsUsed(recordsUsed);
        run.setAccuracy(accuracy);

        log.info(
                "Training run completed successfully [runId={}, ticker={}, recordsUsed={}, accuracy={}]",
                runId,
                run.getTicker(),
                recordsUsed,
                accuracy
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long runId, String errorMessage) {
        ModelTrainingRun run = repository.findById(runId)
                .orElseThrow();

        run.setFinishedAt(LocalDateTime.now());
        run.setStatus(TrainingStatus.FAILED);
        run.setErrorMessage(errorMessage);
        repository.save(run);

        log.error(
                "Training run failed [runId={}, ticker={}, error={}]",
                runId,
                run.getTicker(),
                errorMessage
        );
    }
}
