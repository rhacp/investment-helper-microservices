package com.anghel.investmenthelper.prediction.service.feature;

import com.anghel.investmenthelper.prediction.exception.InvalidTrainingDataException;
import com.anghel.investmenthelper.prediction.model.dto.MarketPriceResponseDTO;
import com.anghel.investmenthelper.prediction.model.internal.PredictionRow;
import com.anghel.investmenthelper.prediction.model.internal.TrainingRow;
import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FeatureEngineeringServiceImpl implements FeatureEngineeringService {

    private static final int SHORT_MOVING_AVERAGE_PERIOD = 5;
    private static final int LONG_MOVING_AVERAGE_PERIOD = 20;
    private static final int VOLATILITY_PERIOD = 5;

    private static final int MIN_TRAINING_RECORDS = LONG_MOVING_AVERAGE_PERIOD + 1;
    private static final int MIN_PREDICTION_RECORDS = LONG_MOVING_AVERAGE_PERIOD;

    @Override
    public List<TrainingRow> buildTrainingRows(List<MarketPriceResponseDTO> marketPriceList) {
        List<TrainingRow> rows = new ArrayList<>();

        if (marketPriceList.size() < MIN_TRAINING_RECORDS) {
            throw new InvalidTrainingDataException(
                    "At least " + MIN_TRAINING_RECORDS + " market price records are required for training");
        }

        for (int i = LONG_MOVING_AVERAGE_PERIOD; i < marketPriceList.size() - 1; i++) {
            MarketPriceResponseDTO current = marketPriceList.get(i);
            MarketPriceResponseDTO next = marketPriceList.get(i + 1);

            double dailyReturn = calculateDailyReturn(marketPriceList.get(i - 1), current);
            double ma5 = calculateMovingAverage(marketPriceList, i, SHORT_MOVING_AVERAGE_PERIOD);
            double ma20 = calculateMovingAverage(marketPriceList, i, LONG_MOVING_AVERAGE_PERIOD);
            double volatility = calculateRollingVolatility(marketPriceList, i, VOLATILITY_PERIOD);
            double volumeChange = calculateVolumeChange(marketPriceList.get(i - 1), current);

            PredictionLabel label = next.getClosePrice().compareTo(current.getClosePrice()) > 0
                    ? PredictionLabel.UP
                    : PredictionLabel.DOWN;

            rows.add(new TrainingRow(
                            dailyReturn,
                            ma5,
                            ma20,
                            volatility,
                            volumeChange,
                            label
                    )
            );
        }

        log.debug("Generated training rows [rows={}]", rows.size());
        return rows;
    }

    @Override
    public PredictionRow buildPredictionRow(List<MarketPriceResponseDTO> marketPriceList) {
        if (marketPriceList.size() < MIN_PREDICTION_RECORDS) {
            throw new InvalidTrainingDataException(
                    "At least " + MIN_PREDICTION_RECORDS + " market price records are required for prediction"
            );
        }

        int lastIndex = marketPriceList.size() - 1;

        MarketPriceResponseDTO current = marketPriceList.get(lastIndex);

        PredictionRow predictionRow = new PredictionRow(
                calculateDailyReturn(marketPriceList.get(lastIndex - 1), current),
                calculateMovingAverage(marketPriceList, lastIndex, SHORT_MOVING_AVERAGE_PERIOD),
                calculateMovingAverage(marketPriceList, lastIndex, LONG_MOVING_AVERAGE_PERIOD),
                calculateRollingVolatility(marketPriceList, lastIndex, VOLATILITY_PERIOD),
                calculateVolumeChange(marketPriceList.get(lastIndex - 1), current)
        );

        log.debug("Generated prediction row [records={}]", marketPriceList.size());
        return predictionRow;
    }

    private double calculateDailyReturn(MarketPriceResponseDTO previous, MarketPriceResponseDTO current) {
        double previousClose = previous.getClosePrice().doubleValue();
        double currentClose = current.getClosePrice().doubleValue();

        if (previousClose == 0) {
            return 0.0;
        }

        return (currentClose - previousClose) / previousClose;
    }

    private double calculateMovingAverage(List<MarketPriceResponseDTO> marketPriceList, int currentIndex, int period) {
        double sum = 0.0;

        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            sum += marketPriceList.get(i)
                    .getClosePrice()
                    .doubleValue();
        }

        return sum / period;
    }

    private double calculateVolumeChange(MarketPriceResponseDTO previous, MarketPriceResponseDTO current) {
        long previousVolume = previous.getVolume();
        long currentVolume = current.getVolume();

        if (previousVolume == 0) {
            return 0.0;
        }

        return (double) (currentVolume - previousVolume) / previousVolume;
    }

    private double calculateRollingVolatility(List<MarketPriceResponseDTO> marketPriceList, int currentIndex,
                                              int period) {

        List<Double> returns = new ArrayList<>();

        for (int i = currentIndex - period + 1; i <= currentIndex; i++) {
            MarketPriceResponseDTO previous = marketPriceList.get(i - 1);
            MarketPriceResponseDTO current = marketPriceList.get(i);
            double dailyReturn = calculateDailyReturn(previous, current);

            returns.add(dailyReturn);
        }

        double mean = returns.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double variance = returns.stream()
                .mapToDouble(
                        value -> Math.pow(value - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }
}
