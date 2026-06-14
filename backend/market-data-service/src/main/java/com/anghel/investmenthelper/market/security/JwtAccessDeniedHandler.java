package com.anghel.investmenthelper.market.security;

import com.anghel.investmenthelper.market.model.dto.ErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage("Access denied");
        errorDTO.setPath(request.getRequestURI());
        errorDTO.setTimestamp(LocalDateTime.now());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            objectMapper.writeValue(response.getOutputStream(), errorDTO);
            log.warn("Access denied [path={}]", request.getRequestURI());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}

