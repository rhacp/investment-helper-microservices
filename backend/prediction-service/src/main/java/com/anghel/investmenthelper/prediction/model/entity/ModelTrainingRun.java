package com.anghel.investmenthelper.prediction.model.entity;

import com.anghel.investmenthelper.prediction.util.enumeration.TrainingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "model_training_run")
public class ModelTrainingRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrainingStatus status;

    @Column(name = "error_message", length = 5000)
    private String errorMessage;

    @Column(name = "records_used")
    private Integer recordsUsed;

    @Column(name = "accuracy")
    private Double accuracy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prediction_model_metadata_id", nullable = false)
    private PredictionModelMetadata predictionModelMetadata;

    @PrePersist
    public void prePersist() {
        status = TrainingStatus.RUNNING;
    }
}
