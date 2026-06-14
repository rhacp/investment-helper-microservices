package com.anghel.investmenthelper.market.repository;

import com.anghel.investmenthelper.market.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock,Long> {

    Stock findStockByTickerIgnoreCase(String ticker);
}
