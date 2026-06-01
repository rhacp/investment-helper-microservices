package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.entity.Holding;

public interface HoldingQueryService {

    Holding getValidHolding(Long id);
}
