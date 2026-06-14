package com.anghel.investmenthelper.market.service.jwt;

import java.security.interfaces.RSAPublicKey;

public interface KeyLoader {

    RSAPublicKey getPublicKey();
}

