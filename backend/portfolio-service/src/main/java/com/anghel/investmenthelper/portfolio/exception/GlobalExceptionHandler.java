package com.anghel.investmenthelper.portfolio.exception;

import com.anghel.investmenthelper.portfolio.model.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){
        Map<String, List<String>> map = new HashMap<>();
        String objectName = exception.getBindingResult().getObjectName();

        exception.getBindingResult().getFieldErrors()
                .forEach(element -> {
                    log.warn("Validation error [path={}, object={}, field={}, message={}]",
                            request.getRequestURI(),
                            objectName,
                            element.getField(),
                            element.getDefaultMessage());

                    map.computeIfAbsent(element.getField(), k -> new ArrayList<>())
                            .add(element.getDefaultMessage());
                });

        return buildErrorResponse("Validation failed", map, request.getRequestURI(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ErrorDTO> handleAccessDenied(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(exception.getMessage(), null, request.getRequestURI(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception exception, HttpServletRequest request){
        log.error("Unexpected error [path={}, message={}]",
                request.getRequestURI(),
                exception.getMessage(),
                exception);

        return buildErrorResponse("Internal server error", null, request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<ErrorDTO> buildErrorResponse(String message, Map<String, List<String>> errors, String path, HttpStatus status) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(message);
        errorDTO.setErrors(errors);
        errorDTO.setPath(path);
        errorDTO.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, status);
    }
}

