package com.anghel.investmenthelper.portfolio.service.jwt;

import java.security.interfaces.RSAPublicKey;

public interface KeyLoader {

    RSAPublicKey getPublicKey();
}

