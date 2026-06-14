package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import com.anghel.investmenthelper.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

@Service
public class PortfolioQueryServiceImpl implements PortfolioQueryService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioQueryServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public Portfolio getValidPortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.getPortfolioById(id);

        if (portfolio == null) {
            throw new ResourceNotFoundException("Portfolio with id " + id + " not found");
        }

        return portfolio;
    }
}
