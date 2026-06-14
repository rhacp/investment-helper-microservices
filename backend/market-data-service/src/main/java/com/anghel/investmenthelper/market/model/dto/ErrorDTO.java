package com.anghel.investmenthelper.market.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    private String message;

    private Map<String, List<String>> errors;

    private String path;

    private LocalDateTime timestamp;
}

