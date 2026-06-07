package com.anghel.investmenthelper.user.service.user;

import com.anghel.investmenthelper.user.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service("userAuthorizationService")
public class UserAuthorizationServiceImpl implements UserAuthorizationService {

    private final UserQueryService userQueryService;

    public UserAuthorizationServiceImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public boolean canAccessUser(Long userId, Authentication authentication) {
        log.debug("Authorities={}", authentication.getAuthorities());
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority()
                        .equals("ROLE_ADMIN"))) {
            return true;
        }

        JwtAuthenticationToken jwt = (JwtAuthenticationToken) authentication;
        Long authUserId = Long.parseLong(jwt.getToken().getSubject());
        User user = userQueryService.getValidUserByAuthUserId(authUserId);

        boolean allowed = user.getAuthUserId().equals(authUserId);
        if (!allowed) {
            log.warn(
                    "Unauthorized profile access attempt [requestedUserId={}, authUserId={}]",
                    userId,
                    authUserId
            );
        }

        return allowed;
    }
}
