package com.anghel.investmenthelper.market.service.market_price;

import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.entity.Stock;

import java.util.List;

public interface MarketPriceService {

    void syncMarketPrices(Stock stock);

    List<MarketPriceResponseDTO> getMarketPriceListByStock(Stock stock);

    MarketPriceInternalResponseDTO getMarketPriceByStock(Stock stock);

    MarketPriceResponseDTO getFullMarketPriceByStock(Stock stock);
}
