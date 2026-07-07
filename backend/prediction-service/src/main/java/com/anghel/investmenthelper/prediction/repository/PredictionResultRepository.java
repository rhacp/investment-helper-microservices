package com.anghel.investmenthelper.prediction.repository;

import com.anghel.investmenthelper.prediction.model.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

    List<PredictionResult> findAllByPredictionForDateAndCorrectIsNull(LocalDate predictionForDate);

    PredictionResult findTopByTickerOrderByCreatedAtDesc(String ticker);

    List<PredictionResult> findAllByTicker(String ticker);

    PredictionResult findByTickerIgnoreCaseAndPredictionForDateAndModelVersion(
            String ticker,
            LocalDate predictionForDate,
            Integer modelVersion);

    @Query("""
       SELECT MAX(p.predictionForDate)
       FROM PredictionResult p
       WHERE p.correct IS NOT NULL
       """)
    LocalDate findLatestValidatedPredictionDate();

    List<PredictionResult> findAllByPredictionForDateAndCorrectIsNotNull(LocalDate predictionForDate);

    List<PredictionResult> findAllByOrderByPredictionForDateDesc();

    List<PredictionResult> findAllByTickerIgnoreCaseOrderByPredictionForDateDesc(String ticker);
}
