package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockTickerResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.SyncStockRequestDTO;

import java.util.List;
import java.util.Map;

public interface StockService {

    StockResponseDTO syncStock(SyncStockRequestDTO request);

    StockResponseDTO getStockByTicker(String ticker);

    List<MarketPriceResponseDTO> getHistoryByTicker(String ticker);

    MarketPriceInternalResponseDTO getMarketPriceByTicker(String ticker);

    void syncAllStocks();

    List<StockTickerResponseDTO> getAllStocks();

    MarketPriceResponseDTO getFullMarketPriceByTicker(String ticker);

    Map<String, MarketPriceInternalResponseDTO> getLatestPrices(List<String> tickers);
}
