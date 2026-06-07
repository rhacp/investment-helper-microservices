package com.anghel.investmenthelper.portfolio.service.holding;

import com.anghel.investmenthelper.portfolio.model.dto.holding.CreateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.HoldingResponseDTO;
import com.anghel.investmenthelper.portfolio.model.dto.holding.UpdateHoldingRequestDTO;
import com.anghel.investmenthelper.portfolio.model.entity.Holding;
import com.anghel.investmenthelper.portfolio.model.entity.Portfolio;
import com.anghel.investmenthelper.portfolio.repository.HoldingRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class HoldingServiceImpl implements HoldingService {

    private final ModelMapper modelMapper;

    private final HoldingRepository holdingRepository;

    private final HoldingQueryService holdingQueryService;

    public HoldingServiceImpl(ModelMapper modelMapper, HoldingRepository holdingRepository, HoldingQueryService holdingQueryService) {
        this.modelMapper = modelMapper;
        this.holdingRepository = holdingRepository;
        this.holdingQueryService = holdingQueryService;
    }

    @Transactional
    @Override
    public Holding createHolding(CreateHoldingRequestDTO createHoldingRequestDTO,
                                            Portfolio portfolio) {
        Holding holding = modelMapper.map(createHoldingRequestDTO, Holding.class);

        holding.setPortfolio(portfolio);
        Holding savedHolding = holdingRepository.save(holding);
        log.info(
                "Holding added to portfolio [holdingId={}, portfolioId={}]",
                savedHolding.getId(),
                portfolio.getId()
        );

        return savedHolding;
    }

    @Transactional
    @Override
    public HoldingResponseDTO updateHoldingById(UpdateHoldingRequestDTO updateHoldingRequestDTO,
                                                Long holdingId) {
        Holding holding = holdingQueryService.getValidHolding(holdingId);
        updateHoldingFromDTO(updateHoldingRequestDTO, holding);

        Holding savedHolding = holdingRepository.save(holding);
        log.info("Holding updated [id={}]", savedHolding.getId());

        return modelMapper.map(savedHolding, HoldingResponseDTO.class);
    }

    @Transactional
    @Override
    public void deleteHoldingById(Long holdingId) {
        Holding holding = holdingQueryService.getValidHolding(holdingId);
        holdingRepository.delete(holding);
        log.info("Holding deleted [id={}]", holdingId);
    }

    private void updateHoldingFromDTO(UpdateHoldingRequestDTO updateHoldingRequestDTO, Holding holding) {
        if (updateHoldingRequestDTO.getQuantity() != null) {
            holding.setQuantity(updateHoldingRequestDTO.getQuantity());
        }

        if (updateHoldingRequestDTO.getAverageBuyPrice() != null) {
            holding.setAverageBuyPrice(updateHoldingRequestDTO.getAverageBuyPrice());
        }

        if (updateHoldingRequestDTO.getPurchaseDate() != null) {
            holding.setPurchaseDate(updateHoldingRequestDTO.getPurchaseDate());
        }
    }
}
