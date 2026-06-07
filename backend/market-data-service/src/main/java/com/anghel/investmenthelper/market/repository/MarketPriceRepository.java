package com.anghel.investmenthelper.market.repository;

import com.anghel.investmenthelper.market.model.entity.MarketPrice;
import com.anghel.investmenthelper.market.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {

    List<MarketPrice> findAllByStockOrderByDateAsc(Stock stock);

    Optional<MarketPrice> findTopByStockOrderByDateDesc(Stock stock);

    boolean existsByStockAndDate(Stock stock, LocalDate date);

    LocalDate findLatestPriceDate(Stock stock);

    @Query("""
                select mp.priceDate
                from MarketPrice mp
                where mp.stock = :stock
            """)
    Set<LocalDate> findAllPriceDatesByStock(
            @Param("stock") Stock stock);
}
