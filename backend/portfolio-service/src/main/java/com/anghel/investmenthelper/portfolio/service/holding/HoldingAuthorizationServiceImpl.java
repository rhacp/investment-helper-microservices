package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service("holdingAuthorizationService")
public class HoldingAuthorizationServiceImpl implements HoldingAuthorizationService {

    private final HoldingQueryService holdingQueryService;

    public HoldingAuthorizationServiceImpl(HoldingQueryService holdingQueryService) {
        this.holdingQueryService = holdingQueryService;
    }

    @Override
    public boolean canAccessHolding(Long holdingId, Authentication authentication) {
        log.debug("Authorities={}", authentication.getAuthorities());
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority()
                        .equals("ROLE_ADMIN"))) {
            return true;
        }

        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        Long authUserId = Long.parseLong(jwt.getToken().getSubject());
        Holding holding = holdingQueryService.getValidHolding(holdingId);

        boolean allowed = holding.getPortfolio().getAuthUserId().equals(authUserId);
        if (!allowed) {
            log.warn(
                    "Unauthorized holding access attempt [holdingId={}, holdingOwnerAuthUserId={}, requesterAuthUserId={}]",
                    holdingId,
                    holding.getPortfolio().getAuthUserId(),
                    authUserId
            );
        }

        return allowed;
    }
}
