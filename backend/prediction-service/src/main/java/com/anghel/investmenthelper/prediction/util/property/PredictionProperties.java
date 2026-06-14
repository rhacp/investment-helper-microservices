package com.anghel.investmenthelper.prediction.util.property;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "prediction")
public class PredictionProperties {

    private Integer minimumTrainingRecords;

    private String modelStoragePath;

    private Double trainingRatio;

    @PostConstruct
    void validate() {
        if (trainingRatio <= 0 || trainingRatio >= 1) {
            throw new IllegalStateException("training-ratio must be between 0 and 1");
        }
    }
}
