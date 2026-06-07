package com.anghel.investmenthelper.portfolio.controller;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.UpdateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.service.holding.HoldingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/holdings")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @DeleteMapping("/{holdingId}")
    @PreAuthorize("@holdingAuthorizationService.canAccessHolding(#holdingId, authentication)")
    public ResponseEntity<Void> deleteHolding(@PathVariable Long holdingId) {
        holdingService.deleteHoldingById(holdingId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{holdingId}")
    @PreAuthorize("@holdingAuthorizationService.canAccessHolding(#holdingId, authentication)")
    public ResponseEntity<HoldingResponseDTO> updateHoldingById(@PathVariable Long holdingId,
                                                                @Valid @RequestBody UpdateHoldingRequestDTO updateHoldingRequestDTO) {
        return ResponseEntity.ok(holdingService.updateHoldingById(updateHoldingRequestDTO, holdingId));
    }
}
