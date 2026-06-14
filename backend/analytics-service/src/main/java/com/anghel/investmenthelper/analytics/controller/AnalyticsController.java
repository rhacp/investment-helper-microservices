package com.anghel.investmenthelper.analytics.controller;

import com.anghel.investmenthelper.analytics.model.dto.response.PortfolioAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.model.dto.response.StockAnalyticsResponseDTO;
import com.anghel.investmenthelper.analytics.service.portfolio.PortfolioAnalyticsService;
import com.anghel.investmenthelper.analytics.service.stock.StockAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final StockAnalyticsService stockAnalyticsService;

    private final PortfolioAnalyticsService portfolioAnalyticsService;

    @GetMapping("/stocks/{ticker}")
    public ResponseEntity<StockAnalyticsResponseDTO> getStockAnalytics(
            @PathVariable String ticker
    ) {
        return ResponseEntity.ok(stockAnalyticsService.getStockAnalytics(ticker));
    }

    @GetMapping("/portfolios/{portfolioId}")
    public ResponseEntity<PortfolioAnalyticsResponseDTO> getPortfolioAnalytics(
            @PathVariable Long portfolioId
    ) {
        return ResponseEntity.ok(portfolioAnalyticsService.getPortfolioAnalytics(portfolioId));
    }
}
