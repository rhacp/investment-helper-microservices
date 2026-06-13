package com.anghel.investmenthelper.market.controller;

import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockTickerResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.SyncStockRequestDTO;
import com.anghel.investmenthelper.market.service.stock.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stocks/{ticker}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StockResponseDTO> getStockByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(stockService.getStockByTicker(ticker));
    }

    @GetMapping("/stocks/{ticker}/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MarketPriceResponseDTO>> getHistoryByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(stockService.getHistoryByTicker(ticker));
    }

    @PostMapping("/stocks/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDTO> syncStock(@Valid @RequestBody SyncStockRequestDTO syncStockRequestDTO) {
        return ResponseEntity.ok(stockService.syncStock(syncStockRequestDTO));
    }

    @GetMapping("/internal/stocks/{ticker}/price")
    public ResponseEntity<MarketPriceInternalResponseDTO> getMarketPriceByTicker(@PathVariable String ticker) {
        return ResponseEntity.ok(stockService.getMarketPriceByTicker(ticker));
    }

    @GetMapping("/internal/stocks/{ticker}/history")
    public ResponseEntity<List<MarketPriceResponseDTO>> getHistoryByTickerInternal(@PathVariable String ticker) {
        return ResponseEntity.ok(stockService.getHistoryByTicker(ticker));
    }

    @GetMapping("/internal/stocks")
    public ResponseEntity<List<StockTickerResponseDTO>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }
}
