package com.anghel.investmenthelper.prediction.client;

import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "market-data-service")
public interface MarketDataClient {

    @GetMapping("/api/v1/internal/stocks/{ticker}/history")
    List<MarketPriceResponseDTO> getStockHistoryByTicker(@PathVariable String ticker);

    @GetMapping("/api/v1/internal/stocks/{ticker}/price")
    MarketPriceResponseDTO getPriceByTicker(@PathVariable String ticker);
}
