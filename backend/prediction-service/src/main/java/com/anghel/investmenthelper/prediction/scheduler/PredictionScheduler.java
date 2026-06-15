package com.anghel.investmenthelper.prediction.scheduler;

import com.anghel.investmenthelper.prediction.client.MarketDataClient;
import com.anghel.investmenthelper.prediction.model.dto.StockTickerResponseDTO;
import com.anghel.investmenthelper.prediction.service.prediction.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PredictionScheduler {

    private final MarketDataClient marketDataClient;

    private final PredictionService predictionService;

    @Scheduled(cron = "0 0 6 * * *")
    public void generatePredictions() {
        List<StockTickerResponseDTO> stocks = marketDataClient.getAllStocks();
        log.info("Starting daily prediction generation [stocks={}]", stocks.size());
        int failures = 0;

        for (StockTickerResponseDTO stock : stocks) {
            try {
                predictionService.generatePrediction(stock.getTicker());
                log.debug("Prediction generated successfully [ticker={}]", stock.getTicker());
            } catch (Exception exception) {
                log.error("Failed prediction generation [ticker={}]", stock.getTicker(), exception);
                failures++;
            }
        }

        log.info("Finished daily prediction generation [stocks={}, failures={}]", stocks.size(), failures);
    }
}
