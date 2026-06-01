package com.anghel.investmenthelper.auth.service.jwt;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;

public interface JwtService {

    String generateAccessToken(AuthUser authUser);
}
