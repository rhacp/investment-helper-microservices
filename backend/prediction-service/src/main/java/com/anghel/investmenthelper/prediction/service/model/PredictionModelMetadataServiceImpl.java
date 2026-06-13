package com.anghel.investmenthelper.prediction.service.model;

import com.anghel.investmenthelper.prediction.client.MarketDataClient;
import com.anghel.investmenthelper.prediction.exception.InvalidTrainingDataException;
import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.training.TrainingModelRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.training.TrainingModelResponseDTO;
import com.anghel.investmenthelper.prediction.model.entity.ModelTrainingRun;
import com.anghel.investmenthelper.prediction.model.entity.PredictionModelMetadata;
import com.anghel.investmenthelper.prediction.model.internal.TrainingRow;
import com.anghel.investmenthelper.prediction.repository.PredictionModelMetadataRepository;
import com.anghel.investmenthelper.prediction.service.feature.FeatureEngineeringService;
import com.anghel.investmenthelper.prediction.service.tribuo.TribuoDatasetService;
import com.anghel.investmenthelper.prediction.util.property.PredictionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tribuo.Model;
import org.tribuo.MutableDataset;
import org.tribuo.Trainer;
import org.tribuo.classification.Label;
import org.tribuo.classification.evaluation.LabelEvaluation;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.classification.sgd.linear.LinearSGDTrainer;
import org.tribuo.classification.sgd.objectives.LogMulticlass;
import org.tribuo.math.optimisers.AdaGrad;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionModelMetadataServiceImpl implements PredictionModelMetadataService {

    private static final int EPOCHS = 10;

    private static final long RANDOM_SEED = 1L;

    private static final double LEARNING_RATE = 0.1;

    private static final int LOGGING_INTERVAL = 1000;

    private static final String MODEL_TYPE = "LINEAR_SGD";

    private final MarketDataClient marketDataClient;

    private final ModelStorageService modelStorageService;

    private final PredictionProperties predictionProperties;

    private final TribuoDatasetService tribuoDatasetService;

    private final ModelTrainingRunService modelTrainingRunService;

    private final FeatureEngineeringService featureEngineeringService;

    private final PredictionModelMetadataRepository predictionModelMetadataRepository;

    @Transactional
    @Override
    public TrainingModelResponseDTO trainModel(TrainingModelRequestDTO request) {
        String ticker = request.getTicker().toUpperCase();
        ModelTrainingRun run = modelTrainingRunService.createRun(ticker);
        String modelPath = null;

        try {
            List<MarketPriceResponseDTO> history = marketDataClient.getStockHistoryByTicker(ticker);
            List<TrainingRow> trainingRows = featureEngineeringService.buildTrainingRows(history);

            if (trainingRows.size() < predictionProperties.getMinimumTrainingRecords()) {
                throw new InvalidTrainingDataException("At least " + predictionProperties.getMinimumTrainingRecords()
                                + " training rows are required");
            }

            int splitIndex = (int)(trainingRows.size() * predictionProperties.getTrainingRatio());

            List<TrainingRow> trainRows = trainingRows.subList(0, splitIndex);
            List<TrainingRow> testRows = trainingRows.subList(splitIndex, trainingRows.size());

            MutableDataset<Label> trainDataset = tribuoDatasetService.buildDataset(trainRows);
            MutableDataset<Label> testDataset = tribuoDatasetService.buildDataset(testRows);

            Trainer<Label> trainer = new LinearSGDTrainer(
                    new LogMulticlass(),
                    new AdaGrad(LEARNING_RATE),
                    EPOCHS,
                    LOGGING_INTERVAL,
                    RANDOM_SEED
            );

            log.info(
                    "Starting model training [ticker={}, trainRecords={}, testRecords={}]",
                    ticker,
                    trainDataset.size(),
                    testDataset.size()
            );

            Model<Label> model = trainer.train(trainDataset);

            LabelEvaluator evaluator = new LabelEvaluator();
            LabelEvaluation evaluation = evaluator.evaluate(model, testDataset);
            double accuracy = evaluation.accuracy();

            PredictionModelMetadata latestModel = predictionModelMetadataRepository.findTopByTickerOrderByModelVersionDesc(ticker);
            int version = latestModel == null ? 1 : latestModel.getModelVersion() + 1;

            modelPath = modelStorageService.saveModel(ticker, model, version);

            List<PredictionModelMetadata> activeModels = predictionModelMetadataRepository.findAllByTickerIgnoreCaseAndActiveTrue(ticker);
            activeModels.forEach(metadata -> metadata.setActive(false));
            predictionModelMetadataRepository.saveAll(activeModels);

            PredictionModelMetadata metadata = new PredictionModelMetadata();
            metadata.setTicker(ticker);
            metadata.setModelVersion(version);
            metadata.setModelType(MODEL_TYPE);
            metadata.setModelPath(modelPath);
            metadata.setAccuracy(accuracy);
            metadata.setRecordsUsed(trainingRows.size());
            metadata.setTrainedAt(LocalDateTime.now());
            metadata.setActive(true);

            PredictionModelMetadata savedMetadata = predictionModelMetadataRepository.save(metadata);
            modelTrainingRunService.markSuccess(run.getId(), savedMetadata, trainingRows.size(), accuracy);

            log.info("Model trained successfully [ticker={}, version={}, recordsUsed={}, accuracy={}]",
                    ticker,
                    version,
                    trainingRows.size(),
                    accuracy
            );

            return new TrainingModelResponseDTO(
                    ticker,
                    savedMetadata.getTrainedAt(),
                    trainingRows.size(),
                    accuracy
            );

        } catch (Exception e) {
            if (modelPath != null) {
                modelStorageService.deleteModel(modelPath);
            }

            modelTrainingRunService.markFailed(run.getId(), e.getMessage());
            log.error("Model training failed [ticker={}]", ticker, e);
            throw e;
        }
    }
}
