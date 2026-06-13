package com.anghel.investmenthelper.prediction.repository;

import com.anghel.investmenthelper.prediction.model.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    List<PredictionResult> findAllByPredictionForDateAndCorrectIsNull(LocalDate predictionForDate);

    PredictionResult findTopByTickerOrderByCreatedAtDesc(String ticker);

    List<PredictionResult> findAllByTicker(String ticker);
}
