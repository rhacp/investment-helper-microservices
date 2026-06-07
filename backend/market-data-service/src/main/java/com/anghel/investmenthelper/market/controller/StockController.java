package com.anghel.investmenthelper.market.controller;

import com.anghel.investmenthelper.market.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.SyncStockRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock")
public class StockController {

    @GetMapping("/{ticker}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StockResponseDTO> getStockByTicker(@PathVariable String ticker) {
        return null;
    }

    @GetMapping("/{ticker}/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MarketPriceResponseDTO>> getHistoryByTicker(@PathVariable String ticker) {
        return null;
    }

    @PostMapping("/sync")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockResponseDTO> syncStock(@Valid @RequestBody SyncStockRequestDTO syncStockRequestDTO) {
        return null;
    }
}
