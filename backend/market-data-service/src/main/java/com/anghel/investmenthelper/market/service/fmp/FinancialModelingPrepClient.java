package com.anghel.investmenthelper.market.service.fmp;

import com.anghel.investmenthelper.market.model.dto.fmp.FinancialModelingPrepHistoricalPriceDTO;
import com.anghel.investmenthelper.market.model.dto.fmp.FinancialModelingPrepProfileDTO;

import java.time.LocalDate;
import java.util.List;

public interface FinancialModelingPrepClient {

    FinancialModelingPrepProfileDTO getStockProfile(String ticker);

    List<FinancialModelingPrepHistoricalPriceDTO> getHistoricalPrices(String ticker, LocalDate startDate, LocalDate endDate);

    FinancialModelingPrepHistoricalPriceDTO getHistoricalPriceForDate(String ticker, LocalDate date);

    FinancialModelingPrepHistoricalPriceDTO getLatestHistoricalPrice(String ticker);
}
