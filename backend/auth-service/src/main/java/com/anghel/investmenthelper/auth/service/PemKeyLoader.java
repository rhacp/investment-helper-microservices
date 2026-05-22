package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.util.property.JwtProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class PemKeyLoader implements KeyLoader {

    private final JwtProperties jwtProperties;

    private final ResourceLoader resourceLoader;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    public PemKeyLoader(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
        this.jwtProperties = jwtProperties;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void init() {
        log.info("Loading RSA private key");
        this.privateKey = loadPrivateKey(jwtProperties.getPrivateKeyPath());
        log.info("RSA private key loaded successfully");

        log.info("Loading RSA public key");
        this.publicKey = loadPublicKey(jwtProperties.getPublicKeyPath());
        log.info("RSA public key loaded successfully");
    }

    @Override
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PrivateKey loadPrivateKey(String path) {
        try {
            String key = readKey(path);

            key = key
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(spec);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load private key", exception);
        }
    }

    private PublicKey loadPublicKey(String path) {
        try {
            String key = readKey(path);

            key = key
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);
        } catch (Exception exception) {
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
