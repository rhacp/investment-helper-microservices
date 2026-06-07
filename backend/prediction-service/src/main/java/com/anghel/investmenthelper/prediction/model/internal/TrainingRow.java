package com.anghel.investmenthelper.prediction.model.internal;

import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrainingRow {

    private final Double dailyReturn;

    private final Double movingAverage5;

    private final Double movingAverage20;

    private final Double volatility5;

    private final PredictionLabel predictionLabel;
}
