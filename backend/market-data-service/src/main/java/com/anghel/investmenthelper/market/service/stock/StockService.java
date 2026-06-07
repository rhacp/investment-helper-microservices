package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.model.dto.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.SyncStockRequestDTO;

import java.util.List;

public interface StockService {

    StockResponseDTO syncStock(SyncStockRequestDTO request);

    StockResponseDTO getStockByTicker(String ticker);

    List<MarketPriceResponseDTO> getHistoryByTicker(String ticker);

    MarketPriceInternalResponseDTO getMarketPriceByTicker(String ticker);
}
