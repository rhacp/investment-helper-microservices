package com.anghel.investmenthelper.market.service;

import com.anghel.investmenthelper.market.util.property.JwtProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class PemKeyLoader implements KeyLoader {

    private final JwtProperties jwtProperties;

    private final ResourceLoader resourceLoader;

    private RSAPublicKey publicKey;

    public PemKeyLoader(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
        this.jwtProperties = jwtProperties;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        log.info("Loading RSA public key");
        this.publicKey = loadPublicKey(jwtProperties.getPublicKeyPath());
        log.info("RSA public key loaded successfully");
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    private RSAPublicKey loadPublicKey(String path) {
        try {
            String key = readKey(path);

            key = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey)keyFactory.generatePublic(spec);
        } catch (Exception exception) {
            log.error("Failed to load RSA public key", exception);
            throw new RuntimeException("Failed to load public key", exception);
        }
    }

    private String readKey(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);

        return new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}

