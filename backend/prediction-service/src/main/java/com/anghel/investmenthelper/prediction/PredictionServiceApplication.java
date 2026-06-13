package com.anghel.investmenthelper.prediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients
public class PredictionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictionServiceApplication.class, args);
	}

}
