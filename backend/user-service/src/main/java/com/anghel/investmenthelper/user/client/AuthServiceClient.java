package com.anghel.investmenthelper.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PatchMapping("/api/v1/internal/auth/users/{authUserId}/disable")
    void disableAuthUser(@PathVariable Long authUserId);
}
