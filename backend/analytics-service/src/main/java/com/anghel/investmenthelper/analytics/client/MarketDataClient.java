package com.anghel.investmenthelper.analytics.client;

import com.anghel.investmenthelper.analytics.model.dto.internal.BatchMarketPriceRequestDTO;
import com.anghel.investmenthelper.analytics.model.dto.internal.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.analytics.model.dto.internal.MarketPriceResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "market-data-service")
public interface MarketDataClient {

    @GetMapping("/api/v1/internal/stocks/{ticker}/history")
    List<MarketPriceResponseDTO> getHistoricalPrices(@PathVariable String ticker);

    @GetMapping("/api/v1/internal/stocks/{ticker}/full-price")
    MarketPriceResponseDTO getLatestPrice(@PathVariable String ticker);

    @PostMapping("/api/v1/internal/stocks/prices")
    Map<String, MarketPriceInternalResponseDTO> getLatestPrices(@RequestBody BatchMarketPriceRequestDTO request);
}
