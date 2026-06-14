package com.anghel.investmenthelper.auth.service.jwt;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyLoader {

    PrivateKey getPrivateKey();

    PublicKey getPublicKey();
}
