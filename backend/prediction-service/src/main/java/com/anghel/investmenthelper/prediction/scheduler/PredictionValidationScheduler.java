package com.anghel.investmenthelper.prediction.scheduler;

import com.anghel.investmenthelper.prediction.client.MarketDataClient;
import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.prediction.model.entity.PredictionResult;
import com.anghel.investmenthelper.prediction.repository.PredictionResultRepository;
import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionValidationScheduler {

    private final MarketDataClient marketDataClient;

    private final PredictionResultRepository predictionResultRepository;

    @Scheduled(cron = "0 0 23 * * *")
    public void validatePredictions() {
        LocalDate today = LocalDate.now();
        List<PredictionResult> predictions = predictionResultRepository.findAllByPredictionForDateAndCorrectIsNull(today);

        for (PredictionResult prediction : predictions) {
            MarketPriceResponseDTO currentPrice = marketDataClient.getPriceByTicker(prediction.getTicker());
            PredictionLabel actualLabel = determineActualLabel(prediction, currentPrice);

            prediction.setActualLabel(actualLabel);
            prediction.setCorrect(prediction.getPredictionLabel() == actualLabel);
            prediction.setValidatedOn(LocalDate.now());
        }

        predictionResultRepository.saveAll(predictions);
    }

    private PredictionLabel determineActualLabel(PredictionResult prediction, MarketPriceResponseDTO actualPrice) {
        return actualPrice.getClosePrice().compareTo(prediction.getReferenceClosePrice()) > 0
                ? PredictionLabel.UP
                : PredictionLabel.DOWN;
    }
}
