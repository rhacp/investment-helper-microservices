package com.anghel.investmenthelper.analytics.service.jwt;

import java.security.interfaces.RSAPublicKey;

public interface KeyLoader {

    RSAPublicKey getPublicKey();
}

