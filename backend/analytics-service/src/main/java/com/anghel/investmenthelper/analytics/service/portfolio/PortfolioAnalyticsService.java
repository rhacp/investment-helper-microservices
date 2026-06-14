package com.anghel.investmenthelper.analytics.service.portfolio;

import com.anghel.investmenthelper.analytics.model.dto.response.PortfolioAnalyticsResponseDTO;

public interface PortfolioAnalyticsService {

    PortfolioAnalyticsResponseDTO getPortfolioAnalytics(Long portfolioId);
}
