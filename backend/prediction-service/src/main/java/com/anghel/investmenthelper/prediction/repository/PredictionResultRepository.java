package com.anghel.investmenthelper.prediction.repository;

import com.anghel.investmenthelper.prediction.model.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {

}
