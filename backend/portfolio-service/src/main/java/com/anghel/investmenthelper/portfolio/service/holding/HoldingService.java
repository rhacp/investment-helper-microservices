package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import org.springframework.security.oauth2.jwt.Jwt;

public interface HoldingService {

    HoldingResponseDTO updateHoldingById(CreateHoldingRequestDTO createHoldingRequestDTO,
                                         Long holdingId);

    void deleteHoldingById(Long holdingId);
}
