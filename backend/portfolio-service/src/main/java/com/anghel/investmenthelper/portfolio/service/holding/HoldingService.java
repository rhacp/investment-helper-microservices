package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.UpdateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;

public interface HoldingService {

    Holding createHolding(CreateHoldingRequestDTO createHoldingRequestDTO, Portfolio portfolio);

    HoldingResponseDTO updateHoldingById(UpdateHoldingRequestDTO updateHoldingRequestDTO,
                                         Long holdingId);

    void deleteHoldingById(Long holdingId);
}
