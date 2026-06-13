package com.anghel.investmenthelper.prediction.model.entity;

import com.anghel.investmenthelper.prediction.util.enumeration.PredictionLabel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "prediction_result", uniqueConstraints = {@UniqueConstraint(columnNames = {
        "ticker",
        "prediction_for_date",
        "model_version"})
})
public class PredictionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker", nullable = false)
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_label", nullable = false)
    private PredictionLabel predictionLabel;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "prediction_for_date")
    private LocalDate predictionForDate;

    @Column(name = "model_version")
    private Integer modelVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "validated_on")
    private LocalDate validatedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "actual_label")
    private PredictionLabel actualLabel;

    @Column(name = "correct")
    private Boolean correct;

    @Column(name = "reference_close_price", nullable = false)
    private BigDecimal referenceClosePrice;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
