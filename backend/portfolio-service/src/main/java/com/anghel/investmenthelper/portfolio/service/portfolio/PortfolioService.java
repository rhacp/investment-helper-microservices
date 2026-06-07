package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.UpdatePortfolioRequestDTO;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface PortfolioService {

    PortfolioResponseDTO createPortfolio(CreatePortfolioRequestDTO createPortfolioRequestDTO, Jwt jwt);

    List<PortfolioResponseDTO> getAllPortfolios(Jwt jwt);

    PortfolioResponseDTO getPortfolioById(Long portfolioId);

    PortfolioResponseDTO updatePortfolioById(UpdatePortfolioRequestDTO updatePortfolioRequestDTO,
                                             Long portfolioId);

    void deletePortfolioById(Long portfolioId);

    HoldingResponseDTO addHoldingToPortfolio(CreateHoldingRequestDTO createHoldingRequestDTO,
                                               Long portfolioId);

    List<HoldingResponseDTO> getHoldingsByPortfolio(Long portfolioId);
}
