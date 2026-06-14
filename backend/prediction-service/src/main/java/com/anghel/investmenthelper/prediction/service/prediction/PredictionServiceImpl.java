package com.anghel.investmenthelper.prediction.service.prediction;

import com.anghel.investmenthelper.prediction.client.MarketDataClient;
import com.anghel.investmenthelper.prediction.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionAnalyticsResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionResponseDTO;
import com.anghel.investmenthelper.prediction.model.entity.PredictionModelMetadata;
import com.anghel.investmenthelper.prediction.model.entity.PredictionResult;
import com.anghel.investmenthelper.prediction.model.internal.PredictionRow;
import com.anghel.investmenthelper.prediction.repository.PredictionModelMetadataRepository;
import com.anghel.investmenthelper.prediction.repository.PredictionResultRepository;
import com.anghel.investmenthelper.prediction.service.feature.FeatureEngineeringService;
import com.anghel.investmenthelper.prediction.service.model.ModelStorageService;
import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tribuo.Example;
import org.tribuo.Feature;
import org.tribuo.Model;
import org.tribuo.Prediction;
import org.tribuo.classification.Label;
import org.tribuo.impl.ArrayExample;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final ModelMapper modelMapper;

    private final MarketDataClient marketDataClient;

    private final ModelStorageService modelStorageService;

    private final FeatureEngineeringService featureEngineeringService;

    private final PredictionResultRepository predictionResultRepository;

    private final PredictionModelMetadataRepository predictionModelMetadataRepository;

    @Transactional
    @Override
    public PredictionResponseDTO predict(PredictionRequestDTO request) {
        String ticker = request.getTicker().toUpperCase();

        PredictionModelMetadata metadata = predictionModelMetadataRepository.findTopByTickerIgnoreCaseAndActiveTrueOrderByModelVersionDesc(ticker);

        if (metadata == null) {
            throw new ResourceNotFoundException("No active prediction model found for ticker " + ticker);
        }

        Model<Label> model = modelStorageService.loadModel(metadata.getModelPath());

        List<MarketPriceResponseDTO> history = marketDataClient.getStockHistoryByTicker(ticker);
        PredictionRow predictionRow = featureEngineeringService.buildPredictionRow(history);

        Example<Label> example = buildPredictionExample(predictionRow);
        Prediction<Label> prediction = model.predict(example);

        PredictionLabel predictionLabel = PredictionLabel.valueOf(prediction.getOutput().getLabel());
        Double confidence = prediction.getOutputScores().get(prediction.getOutput().getLabel()).getScore();

        LocalDate predictionForDate = LocalDate.now().plusDays(1);

        PredictionResult result = new PredictionResult();
        result.setTicker(ticker);
        result.setPredictionLabel(predictionLabel);
        result.setConfidence(confidence);
        result.setPredictionForDate(predictionForDate);
        result.setModelVersion(metadata.getModelVersion());
        result.setReferenceClosePrice(history.getLast().getClosePrice());

        predictionResultRepository.save(result);

        log.info("Prediction generated [ticker={}, prediction={}, confidence={}, modelVersion={}, predictionForDate={}]",
                ticker,
                predictionLabel,
                confidence,
                metadata.getModelVersion(),
                predictionForDate
        );

        return new PredictionResponseDTO(
                ticker,
                predictionLabel,
                confidence,
                metadata.getModelVersion(),
                predictionForDate
        );
    }

    @Override
    public PredictionResponseDTO getLatestPrediction(String ticker) {
        PredictionResult predictionResult = predictionResultRepository.findTopByTickerOrderByCreatedAtDesc(ticker);

        if (predictionResult == null) {
            throw new ResourceNotFoundException("No prediction found for ticker " + ticker);
        }

        log.debug(
                "Latest prediction retrieved [ticker={}, predictionId={}]",
                ticker,
                predictionResult.getId()
        );

        return modelMapper.map(
                predictionResult,
                PredictionResponseDTO.class
        );
    }

    @Override
    public PredictionAnalyticsResponseDTO getAnalytics(String ticker) {
        List<PredictionResult> predictionResultList = predictionResultRepository.findAllByTicker(ticker);

        if (predictionResultList.isEmpty()) {
            throw new ResourceNotFoundException("No prediction history found for ticker " + ticker);
        }

        int totalPredictions = predictionResultList.size();
        int correctPredictions = (int) predictionResultList.stream()
                .filter(prediction -> Boolean.TRUE.equals(prediction.getCorrect()))
                .count();
        double accuracy = (double) correctPredictions / totalPredictions;

        double averageConfidence = predictionResultList.stream()
                .mapToDouble(PredictionResult::getConfidence)
                .average()
                .orElse(0.0);

        PredictionAnalyticsResponseDTO response = new PredictionAnalyticsResponseDTO();
        response.setTicker(ticker);
        response.setTotalPredictions(totalPredictions);
        response.setCorrectPredictions(correctPredictions);
        response.setAccuracy(accuracy);
        response.setAverageConfidence(averageConfidence);

        log.debug(
                "Prediction analytics calculated [ticker={}, totalPredictions={}, accuracy={}]",
                ticker,
                totalPredictions,
                accuracy
        );

        return response;
    }

    private Example<Label> buildPredictionExample(PredictionRow row) {
        ArrayExample<Label> example = new ArrayExample<>(new Label("UNKNOWN"));

        example.add(new Feature("dailyReturn", row.getDailyReturn()));
        example.add(new Feature("movingAverage5", row.getMovingAverage5()));
        example.add(new Feature("movingAverage20", row.getMovingAverage20()));
        example.add(new Feature("volatility5", row.getVolatility5()));
        example.add(new Feature("volumeChange", row.getVolumeChange()));

        return example;
    }
}
