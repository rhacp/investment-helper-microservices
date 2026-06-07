package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.model.entity.Stock;

public interface StockQueryService {

    Stock getValidStock(String ticker);
}
