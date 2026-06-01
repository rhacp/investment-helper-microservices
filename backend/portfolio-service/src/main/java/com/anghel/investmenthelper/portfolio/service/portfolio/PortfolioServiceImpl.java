package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PortfolioServiceImpl implements PortfolioService {

    @Override
    public PortfolioResponseDTO createPortfolio(CreatePortfolioRequestDTO createPortfolioRequestDTO) {
        return null;
    }

    @Override
    public List<PortfolioResponseDTO> getAllPortfolios() {
        return List.of();
    }

    @Override
    public PortfolioResponseDTO getPortfolioById(Long portfolioId) {
        return null;
    }

    @Override
    public PortfolioResponseDTO updatePortfolioById(CreatePortfolioRequestDTO createPortfolioRequestDTO,
                                                    Long id) {
        return null;
    }

    @Override
    public void deletePortfolioById(Long id) {

    }

    @Override
    public HoldingResponseDTO addHoldingToPortfolio(CreateHoldingRequestDTO createHoldingRequestDTO,
                                                      Long portfolioId) {
        return null;
    }

    @Override
    public List<HoldingResponseDTO> getHoldingsByPortfolio(Long portfolioId) {
        return List.of();
    }
}
