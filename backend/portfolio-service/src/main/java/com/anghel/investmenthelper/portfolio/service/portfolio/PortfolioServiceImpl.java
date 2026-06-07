package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.UpdatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import com.anghel.investmenthelper.portfolio.repository.PortfolioRepository;
import com.anghel.investmenthelper.portfolio.service.holding.HoldingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final ModelMapper modelMapper;

    private final PortfolioRepository portfolioRepository;

    private final PortfolioQueryService portfolioQueryService;

    private final HoldingService holdingService;

    public PortfolioServiceImpl(ModelMapper modelMapper, PortfolioRepository portfolioRepository, PortfolioQueryService portfolioQueryService, HoldingService holdingService) {
        this.modelMapper = modelMapper;
        this.portfolioRepository = portfolioRepository;
        this.portfolioQueryService = portfolioQueryService;
        this.holdingService = holdingService;
    }

    @Transactional
    @Override
    public PortfolioResponseDTO createPortfolio(CreatePortfolioRequestDTO createPortfolioRequestDTO,
                                                Jwt jwt) {
        Portfolio portfolio = modelMapper.map(createPortfolioRequestDTO, Portfolio.class);
        portfolio.setAuthUserId(getAuthUserId(jwt));

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        log.info("Portfolio created [id={}]", savedPortfolio.getId());

        PortfolioResponseDTO response = modelMapper.map(savedPortfolio, PortfolioResponseDTO.class);
        response.setTotalValue(BigDecimal.ZERO);
        response.setTotalProfitLoss(BigDecimal.ZERO);
        return response;
    }

    @Override
    public List<PortfolioResponseDTO> getAllPortfolios(Jwt jwt) {
        Long authUserId = getAuthUserId(jwt);
        List<Portfolio> portfolioList = portfolioRepository.findAllByAuthUserId(authUserId);
        log.debug("Retrieved all portfolios [{}] for user [userId={}]",
                portfolioList.size(),
                authUserId);

        return portfolioList.stream()
                .map(this::buildPortfolioResponse)
                .toList();
    }

    @Override
    public PortfolioResponseDTO getPortfolioById(Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Portfolio retrieved [id={}]", portfolioId);

        return buildPortfolioResponse(portfolio);
    }

    @Transactional
    @Override
    public PortfolioResponseDTO updatePortfolioById(UpdatePortfolioRequestDTO updatePortfolioRequestDTO,
                                                    Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Portfolio retrieved [id={}]", portfolioId);

        updatePortfolioFromDTO(updatePortfolioRequestDTO, portfolio);
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        log.info("Portfolio updated [id={}]", savedPortfolio.getId());

        return buildPortfolioResponse(savedPortfolio);
    }

    @Transactional
    @Override
    public void deletePortfolioById(Long portfolioId) {
        portfolioQueryService.getValidPortfolio(portfolioId);
        portfolioRepository.deleteById(portfolioId);
        log.info("Portfolio deleted [id={}]", portfolioId);
    }

    @Override
    public HoldingResponseDTO addHoldingToPortfolio(CreateHoldingRequestDTO createHoldingRequestDTO,
                                                      Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Portfolio retrieved [id={}]", portfolioId);

        Holding holding = holdingService.createHolding(createHoldingRequestDTO, portfolio);

        return modelMapper.map(holding, HoldingResponseDTO.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<HoldingResponseDTO> getHoldingsByPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Portfolio retrieved [id={}]", portfolioId);

        return portfolio.getHoldings().stream()
                .map(holding -> modelMapper.map(holding, HoldingResponseDTO.class))
                .toList();
    }

    private static Long getAuthUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private PortfolioResponseDTO buildPortfolioResponse(Portfolio portfolio) {
        PortfolioResponseDTO portfolioResponseDTO = modelMapper.map(portfolio, PortfolioResponseDTO.class);

        portfolioResponseDTO.setTotalValue(portfolioResponseDTO.getHoldings().stream()
                .map(holding -> holding.getCurrentPrice().multiply(holding.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        portfolioResponseDTO.setTotalProfitLoss(BigDecimal.ZERO);

        return portfolioResponseDTO;
    }

    private void updatePortfolioFromDTO(UpdatePortfolioRequestDTO updatePortfolioRequestDTO, Portfolio portfolio) {
        if (updatePortfolioRequestDTO.getName() != null) {
            portfolio.setName(updatePortfolioRequestDTO.getName());
        }
    }
}
