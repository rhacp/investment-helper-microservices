package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockTickerResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.SyncStockRequestDTO;

import java.util.List;

public interface StockService {

    StockResponseDTO syncStock(SyncStockRequestDTO request);

    StockResponseDTO getStockByTicker(String ticker);

    List<MarketPriceResponseDTO> getHistoryByTicker(String ticker);

    MarketPriceInternalResponseDTO getMarketPriceByTicker(String ticker);

    void syncAllStocks();

    List<StockTickerResponseDTO> getAllStocks();
}
