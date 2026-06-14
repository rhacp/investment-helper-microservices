package com.anghel.investmenthelper.portfolio.service.portfolio;

import org.springframework.security.core.Authentication;

public interface PortfolioAuthorizationService {

    boolean canAccessPortfolio(Long authUserId, Authentication authentication);
}
