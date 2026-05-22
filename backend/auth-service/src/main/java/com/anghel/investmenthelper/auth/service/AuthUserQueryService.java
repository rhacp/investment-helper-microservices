package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;

public interface AuthUserQueryService {

    AuthUser getValidAuthUser(Long id);

    AuthUser getValidAuthUser(String email);
}
