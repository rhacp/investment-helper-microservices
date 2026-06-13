package com.anghel.investmenthelper.prediction.controller;

import com.anghel.investmenthelper.prediction.model.dto.TrainingModelRequestDTO;
import com.anghel.investmenthelper.prediction.model.dto.TrainingModelResponseDTO;
import com.anghel.investmenthelper.prediction.service.model.PredictionModelMetadataService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainController {

    private final PredictionModelMetadataService predictionModelMetadataService;

    public TrainController(PredictionModelMetadataService predictionModelMetadataService) {
        this.predictionModelMetadataService = predictionModelMetadataService;
    }

    @PostMapping("/train")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TrainingModelResponseDTO> trainModel(
            @Valid @RequestBody TrainingModelRequestDTO request) {

        return ResponseEntity.ok(
                predictionModelMetadataService.trainModel(request)
        );
    }
}
