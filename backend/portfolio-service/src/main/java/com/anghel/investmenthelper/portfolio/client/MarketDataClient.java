package com.anghel.investmenthelper.portfolio.client;

import com.anghel.investmenthelper.portfolio.model.dto.internal.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.internal.StockResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.internal.SyncStockRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "market-data-service")
public interface MarketDataClient {

    @PostMapping("/api/v1/internal/stocks/sync")
    StockResponseDTO syncStock(@RequestBody SyncStockRequestDTO request);

    @GetMapping("/api/v1/internal/stocks/{ticker}")
    StockResponseDTO getStockByTicker(@PathVariable("ticker") String ticker);

    @GetMapping("/api/v1/internal/stocks/{ticker}/price")
    MarketPriceInternalResponseDTO getMarketPriceByTicker(@PathVariable("ticker") String ticker);
}
