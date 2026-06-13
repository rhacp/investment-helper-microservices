package com.anghel.investmenthelper.prediction.repository;

import com.anghel.investmenthelper.prediction.model.entity.PredictionModelMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionModelMetadataRepository extends JpaRepository<PredictionModelMetadata, Long> {

    PredictionModelMetadata findTopByTickerOrderByModelVersionDesc(String ticker);

    List<PredictionModelMetadata> findAllByTickerIgnoreCaseAndActiveTrue(String ticker);

    PredictionModelMetadata findTopByTickerIgnoreCaseAndActiveTrueOrderByModelVersionDesc(String ticker);
}
