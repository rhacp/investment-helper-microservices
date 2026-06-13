package com.anghel.investmenthelper.market.client;

import com.anghel.investmenthelper.market.model.dto.TrainingModelRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prediction-service")
public interface PredictionClient {

    @PostMapping("/api/v1/trainings/train")
    void trainModel(@RequestBody TrainingModelRequestDTO request);
}
