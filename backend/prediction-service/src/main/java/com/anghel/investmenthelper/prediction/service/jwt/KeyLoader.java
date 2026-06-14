package com.anghel.investmenthelper.prediction.service.jwt;

import java.security.interfaces.RSAPublicKey;

public interface KeyLoader {

    RSAPublicKey getPublicKey();
}
