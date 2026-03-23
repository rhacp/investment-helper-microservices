package com.anghel.investmenthelper.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    private String message;

    private Map<String, List<String>> errors;

    private String path;

    private LocalDateTime timestamp;
}
