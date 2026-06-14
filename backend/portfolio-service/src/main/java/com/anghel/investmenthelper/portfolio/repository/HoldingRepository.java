package com.anghel.investmenthelper.portfolio.repository;

import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {

    List<Holding> findAllByPortfolioId(Long portfolioId);

    Holding findHoldingById(Long id);
}
