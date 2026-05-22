package com.anghel.investmenthelper.auth.service;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import com.anghel.investmenthelper.auth.util.property.JwtProperties;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private final KeyLoader keyLoader;

    public JwtServiceImpl(JwtProperties jwtProperties, KeyLoader keyLoader) {
        this.jwtProperties = jwtProperties;
        this.keyLoader = keyLoader;
    }

    @Override
    public String generateAccessToken(AuthUser authUser) {
        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime()
                        + jwtProperties.getAccessTokenExpirationSeconds() * 1000
        );

        PrivateKey privateKey = keyLoader.getPrivateKey();

        String accessToken = Jwts.builder()
                .subject(authUser.getId().toString())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(
                        "roles",
                        List.of(authUser.getRole().name())
                )
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        log.info(
                "Access token generated [userId={}, role={}]",
                authUser.getId(),
                authUser.getRole()
        );

        return accessToken;
    }
}
