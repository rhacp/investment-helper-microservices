package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service("portfolioAuthorizationService")
public class PortfolioAuthorizationServiceImpl implements PortfolioAuthorizationService {

    private final PortfolioQueryService portfolioQueryService;

    public PortfolioAuthorizationServiceImpl(PortfolioQueryService portfolioQueryService) {
        this.portfolioQueryService = portfolioQueryService;
    }

    @Override
    public boolean canAccessPortfolio(Long portfolioId, Authentication authentication) {
        log.debug("Authorities={}", authentication.getAuthorities());
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority()
                        .equals("ROLE_ADMIN"))) {
            return true;
        }

        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        Long authUserId = Long.parseLong(jwt.getToken().getSubject());
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);

        boolean allowed = portfolio.getAuthUserId().equals(authUserId);
        if (!allowed) {
            log.warn(
                    "Unauthorized portfolio access attempt [portfolioId={}, portfolioOwnerAuthUserId={}, requesterAuthUserId={}]",
                    portfolioId,
                    portfolio.getAuthUserId(),
                    authUserId
            );
        }

        return allowed;
    }
}
