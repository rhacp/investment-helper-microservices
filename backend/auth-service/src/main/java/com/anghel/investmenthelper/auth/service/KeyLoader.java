package com.anghel.investmenthelper.auth.service;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyLoader {

    PrivateKey getPrivateKey();

    PublicKey getPublicKey();
}
