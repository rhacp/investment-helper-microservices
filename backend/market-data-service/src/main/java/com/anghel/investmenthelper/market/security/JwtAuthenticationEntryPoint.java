package com.anghel.investmenthelper.market.security;

import com.anghel.investmenthelper.market.model.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {

        ErrorDTO errorDTO = new ErrorDTO();
        String message = getMessage(exception);

        errorDTO.setMessage(message);
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        log.warn(
                "Unauthorized request [path={}, message={}]",
                request.getRequestURI(),
                message
        );

        objectMapper.writeValue(response.getOutputStream(), errorDTO);
    }

    private String getMessage(AuthenticationException exception) {
        String message = "Unauthorized";

        String exceptionMessage = exception.getMessage();
        Throwable cause = getRootCause(exception);

        if (exceptionMessage != null
                && (exceptionMessage.contains("Not Authenticated")
                || exceptionMessage.contains("Bearer token is missing"))) {
            return "Missing JWT token";
        }

        if (cause != null) {
            String causeMessage = cause.getMessage();

            if (causeMessage != null) {
                if (causeMessage.contains("Malformed")) {
                    message = "Malformed JWT token";
                } else if (causeMessage.contains("expired")) {
                    message = "Expired JWT token";
                } else if (causeMessage.contains("Invalid signature")) {
                    message = "Invalid JWT signature";
                } else {
                    message = "Invalid JWT token";
                }
            }
        }

        return message;
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable result = throwable;

        while (result.getCause() != null) {
            result = result.getCause();
        }

        return result;
    }
}
