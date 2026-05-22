package com.anghel.investmenthelper.user.service;

import org.springframework.security.core.Authentication;

public interface UserAuthorizationService {

    boolean canAccessUser(Long userId, Authentication authentication);
}
