package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;

public interface JwtService {

    String generateAccessToken(AuthUser authUser);
}
