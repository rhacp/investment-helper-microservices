package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;

public interface PortfolioQueryService {

    Portfolio getValidPortfolio(Long id);
}
