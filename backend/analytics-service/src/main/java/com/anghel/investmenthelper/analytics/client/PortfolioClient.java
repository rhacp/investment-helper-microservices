package com.anghel.investmenthelper.analytics.client;

import com.anghel.investmenthelper.analytics.model.dto.internal.PortfolioDetailsInternalResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "portfolio-service")
public interface PortfolioClient {

    @GetMapping("/api/v1/internal/portfolios/{portfolioId}")
    PortfolioDetailsInternalResponseDTO getPortfolioDetails(@PathVariable Long portfolioId);
}
