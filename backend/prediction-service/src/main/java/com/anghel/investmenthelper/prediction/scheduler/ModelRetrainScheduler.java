package com.anghel.investmenthelper.prediction.scheduler;

import com.anghel.investmenthelper.prediction.client.MarketDataClient;
import com.anghel.investmenthelper.prediction.model.dto.StockTickerResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.training.TrainingModelRequestDTO;
import com.anghel.investmenthelper.prediction.service.model.PredictionModelMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelRetrainScheduler {

    private final MarketDataClient marketDataClient;

    private final PredictionModelMetadataService predictionModelMetadataService;

    @Scheduled(cron = "0 0 5 * * *")
    public void retrainModels() {
        List<StockTickerResponseDTO> stocks = marketDataClient.getAllStocks();
        log.info("Starting scheduled model retraining [stocks={}]", stocks.size());
        int failures = 0;

        for (StockTickerResponseDTO stock : stocks) {
            try {
                predictionModelMetadataService.trainModel(new TrainingModelRequestDTO(stock.getTicker()));
                log.debug("Model retrained successfully [ticker={}]", stock.getTicker());
            } catch (Exception e) {
                log.error("Failed to retrain model [ticker={}]", stock.getTicker(), e);
                failures++;
            }
        }

        log.info("Finished scheduled model retraining [stocks={}, failures={}]", stocks.size(), failures);
    }
}
