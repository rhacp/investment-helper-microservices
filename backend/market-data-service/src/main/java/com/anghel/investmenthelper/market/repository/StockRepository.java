package com.anghel.investmenthelper.market.repository;

import com.anghel.investmenthelper.market.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock,Long> {

    Optional<Stock> findByTickerIgnoreCase(String ticker);

    boolean existsByTickerIgnoreCase(String ticker);
}
