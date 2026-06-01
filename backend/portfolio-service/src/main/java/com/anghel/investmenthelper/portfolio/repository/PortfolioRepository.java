package com.anghel.investmenthelper.portfolio.repository;

import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findAllByAuthUserId(Long authUserId);
}
