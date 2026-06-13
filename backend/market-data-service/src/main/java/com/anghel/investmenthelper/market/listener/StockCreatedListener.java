package com.anghel.investmenthelper.market.listener;

import com.anghel.investmenthelper.market.client.PredictionClient;
import com.anghel.investmenthelper.market.event.StockCreatedEvent;
import com.anghel.investmenthelper.market.model.dto.TrainingModelRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockCreatedListener {

    private final PredictionClient predictionClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(StockCreatedEvent event) {
        try {
            predictionClient.trainModel(new TrainingModelRequestDTO(event.getTicker()));
            log.info("Initial model training triggered [ticker={}]", event.getTicker());
        } catch (Exception e) {
            log.error("Failed to trigger initial model training [ticker={}]", event.getTicker(), e);
        }
    }
}
