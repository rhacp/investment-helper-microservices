package com.anghel.investmenthelper.market.util.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
@EnableScheduling
public class JwtProperties {

    private String issuer;

    private String publicKeyPath;
}

