package com.anghel.investmenthelper.auth.util.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private String issuer;

    private long accessTokenExpirationSeconds;

    private String privateKeyPath;

    private String publicKeyPath;
}
