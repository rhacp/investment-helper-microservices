package com.anghel.investmenthelper.portfolio.client;

import com.anghel.investmenthelper.portfolio.model.dto.internal.PredictionRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prediction-service")
public interface PredictionClient {

    @PostMapping("/api/v1/internal/predictions")
    void predict(@RequestBody PredictionRequestDTO predictionRequestDTO);
}
