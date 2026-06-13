package com.anghel.investmenthelper.prediction.controller;

import com.anghel.investmenthelper.prediction.model.dto.PredictionRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.PredictionResponseDTO;
import com.anghel.investmenthelper.prediction.service.prediction.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vq/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PredictionResponseDTO> predict(
            @Valid @RequestBody PredictionRequestDTO request) {
        return ResponseEntity.ok(predictionService.predict(request));
    }
}
