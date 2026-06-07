package com.anghel.investmenthelper.portfolio.controller;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.UpdatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.service.portfolio.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PortfolioResponseDTO> createPortfolio(@Valid @RequestBody CreatePortfolioRequestDTO createPortfolioRequestDTO,
                                                                @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(portfolioService.createPortfolio(createPortfolioRequestDTO, jwt));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PortfolioResponseDTO>> getAllPortfolios(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(portfolioService.getAllPortfolios(jwt));
    }

    @GetMapping("/{portfolioId}")
    @PreAuthorize("@portfolioAuthorizationService.canAccessPortfolio(#portfolioId, authentication)")
    public ResponseEntity<PortfolioResponseDTO> getPortfolioById(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(portfolioService.getPortfolioById(portfolioId));
    }

    @DeleteMapping("/{portfolioId}")
    @PreAuthorize("@portfolioAuthorizationService.canAccessPortfolio(#portfolioId, authentication)")
    public ResponseEntity<Void> deletePortfolioById(@PathVariable Long portfolioId) {
        portfolioService.deletePortfolioById(portfolioId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{portfolioId}")
    @PreAuthorize("@portfolioAuthorizationService.canAccessPortfolio(#portfolioId, authentication)")
    public ResponseEntity<PortfolioResponseDTO> updatePortfolioById(@PathVariable Long portfolioId,
                                                                    @Valid @RequestBody UpdatePortfolioRequestDTO updatePortfolioRequestDTO) {
        return ResponseEntity.ok(portfolioService.updatePortfolioById(updatePortfolioRequestDTO, portfolioId));
    }

    @PostMapping("/{portfolioId}/holdings")
    @PreAuthorize("@portfolioAuthorizationService.canAccessPortfolio(#portfolioId, authentication)")
    public ResponseEntity<HoldingResponseDTO> addHoldingToPortfolio(@PathVariable Long portfolioId,
                                                                      @Valid @RequestBody CreateHoldingRequestDTO createHoldingRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(portfolioService.addHoldingToPortfolio(createHoldingRequestDTO, portfolioId));
    }

    @GetMapping("/{portfolioId}/holdings")
    @PreAuthorize("@portfolioAuthorizationService.canAccessPortfolio(#portfolioId, authentication)")
    public ResponseEntity<List<HoldingResponseDTO>> getHoldingsByPortfolio(@PathVariable Long portfolioId) {
        return ResponseEntity.ok(portfolioService.getHoldingsByPortfolio(portfolioId));
    }
}
