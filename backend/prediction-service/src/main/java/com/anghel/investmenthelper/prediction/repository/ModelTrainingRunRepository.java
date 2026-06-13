package com.anghel.investmenthelper.prediction.repository;

import com.anghel.investmenthelper.prediction.model.entity.ModelTrainingRun;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelTrainingRunRepository extends JpaRepository<ModelTrainingRun, Long> {

}
