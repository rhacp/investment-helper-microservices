package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface PortfolioService {

    PortfolioResponseDTO createPortfolio(CreatePortfolioRequestDTO createPortfolioRequestDTO);

    List<PortfolioResponseDTO> getAllPortfolios();

    PortfolioResponseDTO getPortfolioById(Long portfolioId);

    PortfolioResponseDTO updatePortfolioById(CreatePortfolioRequestDTO createPortfolioRequestDTO,
                                             Long id);

    void deletePortfolioById(Long id);

    HoldingResponseDTO addHoldingToPortfolio(CreateHoldingRequestDTO createHoldingRequestDTO,
                                               Long portfolioId);

    List<HoldingResponseDTO> getHoldingsByPortfolio(Long portfolioId);
}
