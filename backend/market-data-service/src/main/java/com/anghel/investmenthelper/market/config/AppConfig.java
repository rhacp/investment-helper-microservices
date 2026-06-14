package com.anghel.investmenthelper.market.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestClient financialModelingPrepRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://financialmodelingprep.com")
                .build();
    }
}

