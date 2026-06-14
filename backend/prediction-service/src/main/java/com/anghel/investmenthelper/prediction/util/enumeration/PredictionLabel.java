package com.anghel.investmenthelper.prediction.util.enumeration;

import lombok.Getter;

@Getter
public enum PredictionLabel {

    UP("UP"),
    DOWN("DOWN");

    private final String label;

    PredictionLabel(String label) {
        this.label = label;
    }
}
