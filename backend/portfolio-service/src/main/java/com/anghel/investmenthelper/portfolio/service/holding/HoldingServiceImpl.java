package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HoldingServiceImpl implements HoldingService {


    @Override
    public HoldingResponseDTO updateHoldingById(CreateHoldingRequestDTO createHoldingRequestDTO,
                                                Long holdingId) {
        return null;
    }

    @Override
    public void deleteHoldingById(Long holdingId) {

    }
}
