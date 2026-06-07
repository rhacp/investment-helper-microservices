package com.anghel.investmenthelper.market.repository;

import com.anghel.investmenthelper.market.model.entity.MarketPrice;
import com.anghel.investmenthelper.market.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarketPriceRepository extends JpaRepository<MarketPrice,Long> {

    List<MarketPrice> findAllByStockOrderByDateAsc(Stock stock);

    List<MarketPrice> findAllByStockAndDateBetweenOrderByDateAsc(
            Stock stock,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<MarketPrice> findTopByStockOrderByDateDesc(Stock stock);

    boolean existsByStockAndDate(Stock stock, LocalDate date);
}
