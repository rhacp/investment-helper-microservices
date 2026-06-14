package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import com.anghel.investmenthelper.portfolio.repository.HoldingRepository;
import org.springframework.stereotype.Service;

@Service
public class HoldingQueryServiceImpl implements HoldingQueryService {

    private final HoldingRepository holdingRepository;

    public HoldingQueryServiceImpl(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    @Override
    public Holding getValidHolding(Long id) {
        Holding holding = holdingRepository.findHoldingById(id);

        if (holding == null) {
            throw new ResourceNotFoundException("Holding with id " + id + " not found");
        }

        return holding;
    }
}
