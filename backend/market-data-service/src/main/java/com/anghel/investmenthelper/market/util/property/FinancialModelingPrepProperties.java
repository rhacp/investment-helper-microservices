package com.anghel.investmenthelper.market.util.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("financial-modeling-prep")
public class FinancialModelingPrepProperties {

    private String apiKey;
}
