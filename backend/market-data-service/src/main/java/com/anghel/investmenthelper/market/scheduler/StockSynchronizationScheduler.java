package com.anghel.investmenthelper.market.scheduler;

import com.anghel.investmenthelper.market.service.stock.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockSynchronizationScheduler {

    private final StockService stockService;

    public StockSynchronizationScheduler(StockService stockService) {
        this.stockService = stockService;
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Bucharest")
    public void synchronizeStocks() {
        log.info("Started stock daily synchronization");
        stockService.syncAllStocks();
        log.info("Finished stock daily synchronization");
    }
}
