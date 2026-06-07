package com.anghel.investmenthelper.market.service.market_data;

import com.anghel.investmenthelper.market.model.dto.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.entity.Stock;

import java.util.List;

public interface MarketPriceService {

    void createMarketPriceList(Stock stock);

    List<MarketPriceResponseDTO> getMarketPriceListByStock(Stock stock);

    MarketPriceInternalResponseDTO getMarketPriceByStock(Stock stock);
}
