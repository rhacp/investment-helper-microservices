package com.anghel.investmenthelper.gateway.security;

import com.anghel.investmenthelper.gateway.entity.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        ErrorDTO errorDTO = new ErrorDTO();
        String message = getMessage(exception);

        errorDTO.setMessage(message);
        errorDTO.setPath(exchange.getRequest().getPath().value());
        errorDTO.setTimestamp(LocalDateTime.now());

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorDTO);

            log.warn(
                    "Unauthorized request [path={}, message={}]",
                    exchange.getRequest().getPath().value(),
                    message
            );

            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
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
