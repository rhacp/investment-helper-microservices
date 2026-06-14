package com.anghel.investmenthelper.analytics.service.stock;

import com.anghel.investmenthelper.analytics.model.dto.response.StockAnalyticsResponseDTO;

public interface StockAnalyticsService {

    StockAnalyticsResponseDTO getStockAnalytics(String ticker);
}
