package com.anghel.investmenthelper.prediction.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prediction_model_metadata")
public class PredictionModelMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Column(name = "model_version")
    private Integer modelVersion;

    @Column(name = "model_type")
    private String modelType;

    @Column(name = "model_path", nullable = false)
    private String modelPath;

    @Column(name = "accuracy")
    private Double accuracy;

    @Column(name = "records_used")
    private Integer recordsUsed;

    @Column (name = "trained_at")
    private LocalDateTime trainedAt;

    @Column (name = "active", nullable = false)
    private Boolean active;

    @PrePersist
    protected void init() {
        active = true;
    }
}
