package com.anghel.investmenthelper.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @PostMapping("/api/v1/internal/auth-users/{authUserId}/disable")
    void disableAuthUser(@PathVariable Long authUserId);
}
