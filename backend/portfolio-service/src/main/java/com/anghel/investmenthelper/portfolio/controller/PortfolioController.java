package com.anghel.investmenthelper.portfolio.controller;

import com.anghel.investmenthelper.portfolio.model.dto.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.PortfolioResponseDTO;
import com.anghel.investmenthelper.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDTO> createPortfolio(@Valid @RequestBody CreatePortfolioRequestDTO createPortfolioRequestDTO) {
        return null;
    }
}
