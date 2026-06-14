package com.anghel.investmenthelper.prediction.controller;

import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionAnalyticsResponseDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.prediction.PredictionResponseDTO;
import com.anghel.investmenthelper.prediction.service.prediction.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/predictions")
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

    @GetMapping("/{ticker}/latest")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PredictionResponseDTO> getLatestPrediction(
            @PathVariable String ticker) {
        return ResponseEntity.ok(predictionService.getLatestPrediction(ticker));
    }

    @GetMapping("/{ticker}/analytics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PredictionAnalyticsResponseDTO> getAnalytics(
            @PathVariable String ticker) {
        return ResponseEntity.ok(predictionService.getAnalytics(ticker));
    }
}
