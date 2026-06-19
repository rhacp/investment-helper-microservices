package com.anghel.investmenthelper.gateway.security;

import com.anghel.investmenthelper.gateway.entity.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setMessage("Access denied");
        errorDTO.setPath(exchange.getRequest().getPath().value());
        errorDTO.setTimestamp(LocalDateTime.now());

        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        response.getHeaders().add(
                "Access-Control-Allow-Origin",
                "http://localhost:3000"
        );

        response.getHeaders().add(
                "Access-Control-Allow-Credentials",
                "true"
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorDTO);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
