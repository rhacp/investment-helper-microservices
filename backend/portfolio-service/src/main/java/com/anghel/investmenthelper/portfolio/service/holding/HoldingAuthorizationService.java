package com.anghel.investmenthelper.portfolio.service.holding;

import org.springframework.security.core.Authentication;

public interface HoldingAuthorizationService {

    boolean canAccessHolding(Long holdingId, Authentication authentication);
}
