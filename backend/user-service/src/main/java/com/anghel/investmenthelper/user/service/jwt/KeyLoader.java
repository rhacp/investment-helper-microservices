package com.anghel.investmenthelper.user.service.jwt;

import java.security.interfaces.RSAPublicKey;

public interface KeyLoader {

    RSAPublicKey getPublicKey();
}
