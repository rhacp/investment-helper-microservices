package com.anghel.investmenthelper.portfolio.service.portfolio;

import com.anghel.investmenthelper.portfolio.client.MarketDataClient;
import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.internal.HoldingInternalResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.internal.PortfolioDetailsInternalResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.CreatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.PortfolioResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.portfolio.UpdatePortfolioRequestDTO;
import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import com.anghel.investmenthelper.portfolio.repository.PortfolioRepository;
import com.anghel.investmenthelper.portfolio.service.holding.HoldingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final ModelMapper modelMapper;

    private final PortfolioRepository portfolioRepository;

    private final PortfolioQueryService portfolioQueryService;

    private final HoldingService holdingService;

    private final MarketDataClient marketDataClient;

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

        return buildHoldingResponse(holding);
    }

    @Transactional(readOnly = true)
    @Override
    public List<HoldingResponseDTO> getHoldingsByPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Portfolio retrieved [id={}]", portfolioId);

        return portfolio.getHoldings().stream()
                .map(this::buildHoldingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PortfolioDetailsInternalResponseDTO getPortfolioDetailsInternal(Long portfolioId) {
        Portfolio portfolio = portfolioQueryService.getValidPortfolio(portfolioId);
        log.debug("Internal portfolio details retrieved [portfolioId={}]", portfolioId);

        return new PortfolioDetailsInternalResponseDTO(
                portfolio.getId(),
                portfolio.getName(),
                portfolio.getAuthUserId(),
                portfolio.getHoldings()
                        .stream()
                        .map(this::mapHoldingInternal)
                        .toList()
        );
    }

    private HoldingInternalResponseDTO mapHoldingInternal(Holding holding) {
        return new HoldingInternalResponseDTO(
                holding.getTicker(),
                holding.getQuantity(),
                holding.getAverageBuyPrice()
        );
    }

    private static Long getAuthUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private PortfolioResponseDTO buildPortfolioResponse(Portfolio portfolio) {
        PortfolioResponseDTO response = modelMapper.map(portfolio, PortfolioResponseDTO.class);

        List<HoldingResponseDTO> holdings = portfolio.getHoldings().stream()
                .map(this::buildHoldingResponse)
                .toList();
        response.setHoldings(holdings);

        response.setTotalValue(holdings.stream()
                .map(HoldingResponseDTO::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        response.setTotalProfitLoss(holdings.stream()
                .map(HoldingResponseDTO::getProfitLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return response;
    }

    private void updatePortfolioFromDTO(UpdatePortfolioRequestDTO updatePortfolioRequestDTO, Portfolio portfolio) {
        if (updatePortfolioRequestDTO.getName() != null) {
            portfolio.setName(updatePortfolioRequestDTO.getName());
        }
    }

    private HoldingResponseDTO buildHoldingResponse(Holding holding) {
        HoldingResponseDTO response = modelMapper.map(holding, HoldingResponseDTO.class);

        BigDecimal currentPrice = marketDataClient.getMarketPriceByTicker(holding.getTicker()).getPrice();
        BigDecimal currentValue = currentPrice.multiply(holding.getQuantity());
        BigDecimal investedValue = holding.getAverageBuyPrice().multiply(holding.getQuantity());
        BigDecimal profitLoss = currentValue.subtract(investedValue);
        BigDecimal profitPercentage = investedValue.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : profitLoss.divide(investedValue, 8, RoundingMode.HALF_UP);

        response.setCurrentPrice(currentPrice);
        response.setCurrentValue(currentValue);
        response.setProfitLoss(profitLoss);
        response.setProfitPercentage(profitPercentage);

        return response;
    }
}
