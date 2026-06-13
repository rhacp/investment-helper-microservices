package com.anghel.investmenthelper.prediction.model.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PredictionRow {

    private final Double dailyReturn;

    private final Double movingAverage5;

    private final Double movingAverage20;

    private final Double volatility5;

    private final Double volumeChange;
}
