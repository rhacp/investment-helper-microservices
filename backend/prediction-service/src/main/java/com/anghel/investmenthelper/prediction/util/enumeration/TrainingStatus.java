package com.anghel.investmenthelper.prediction.util.enumeration;

import lombok.Getter;

@Getter
public enum TrainingStatus {
    RUNNING("RUNNING"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED");

    private final String label;

    TrainingStatus(String label) {
        this.label = label;
    }
}
