package com.anghel.investmenthelper.market.service.market_price;

import com.anghel.investmenthelper.market.exception.FinancialModelingPrepException;
import com.anghel.investmenthelper.market.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.market.model.dto.fmp.FinancialModelingPrepHistoricalPriceDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.entity.MarketPrice;
import com.anghel.investmenthelper.market.model.entity.Stock;
import com.anghel.investmenthelper.market.repository.MarketPriceRepository;
import com.anghel.investmenthelper.market.service.fmp.FinancialModelingPrepClient;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketPriceServiceImpl implements MarketPriceService {

    private final ModelMapper modelMapper;

    private final FinancialModelingPrepClient financialModelingPrepClient;

    private final MarketPriceRepository marketPriceRepository;

    @Transactional
    @Override
    public void performInitialSynchronization(Stock stock) {
        LocalDate latestPriceDate = marketPriceRepository.findLatestPriceDate(stock);
        if (latestPriceDate != null) {
            log.debug("Initial synchronization skipped because market prices already exist [ticker={}, latestPriceDate={}]", stock.getTicker(), latestPriceDate);
            return;
        }

        List<FinancialModelingPrepHistoricalPriceDTO> historicalPrices = financialModelingPrepClient.getHistoricalPrices(
                stock.getTicker(),
                LocalDate.now().minusYears(2),
                LocalDate.now()
        );

        List<MarketPrice> marketPrices = historicalPrices.stream()
                .map(this::mapHistoricalPrice)
                .peek(price -> price.setStock(stock))
                .toList();

        List<MarketPrice> savedPrices = marketPriceRepository.saveAll(marketPrices);
        log.info(
                "Initial market price synchronization completed [ticker={}, importedRecords={}]",
                stock.getTicker(),
                savedPrices.size()
        );
    }

    @Transactional
    @Override
    public void synchronizeLatestPrice(Stock stock) {
        LocalDate latestPriceDate = marketPriceRepository.findLatestPriceDate(stock);

        FinancialModelingPrepHistoricalPriceDTO dto = financialModelingPrepClient
                .getLatestHistoricalPrice(stock.getTicker()
        );

        if (latestPriceDate != null && !dto.getDate().isAfter(latestPriceDate)){
            log.debug(
                    "Skipping latest price synchronization [ticker={}, latestDbDate={}, providerDate={}]",
                    stock.getTicker(),
                    latestPriceDate,
                    dto.getDate()
            );
            return;
        }

        MarketPrice marketPrice = mapHistoricalPrice(dto);
        marketPrice.setStock(stock);
        marketPriceRepository.save(marketPrice);

        log.info(
                "Latest market price synchronized [ticker={}, date={}, closePrice={}]",
                stock.getTicker(),
                marketPrice.getPriceDate(),
                marketPrice.getClosePrice()
        );
    }

    @Override
    public List<MarketPriceResponseDTO> getMarketPriceListByStock(Stock stock) {
        List<MarketPrice> marketPriceList = marketPriceRepository.findAllByStockOrderByPriceDateAsc(stock);
        log.debug("MarketPrice list retrieved for stock [ticker={}]", stock.getTicker());

        return marketPriceList.stream()
                .map(marketPrice -> modelMapper.map(marketPrice, MarketPriceResponseDTO.class))
                .toList();
    }

    @Override
    public MarketPriceInternalResponseDTO getMarketPriceByStock(Stock stock) {
        MarketPrice marketPrice = marketPriceRepository.findTopByStockOrderByPriceDateDesc(stock)
                .orElseThrow(() -> new ResourceNotFoundException("Market Price not found for stock " + stock.getTicker()));

        log.debug("MarketPrice retrieved for ticker [ticker={}]", stock.getTicker());

        return new MarketPriceInternalResponseDTO(
                marketPrice.getStock().getTicker(),
                marketPrice.getClosePrice());
    }

    @Override
    public MarketPriceResponseDTO getFullMarketPriceByStock(Stock stock) {
        MarketPrice marketPrice = marketPriceRepository.findTopByStockOrderByPriceDateDesc(stock)
                .orElseThrow(() -> new ResourceNotFoundException("Market Price not found for stock " + stock.getTicker()));

        log.debug("MarketPrice retrieved for ticker [ticker={}]", stock.getTicker());

        return modelMapper.map(marketPrice, MarketPriceResponseDTO.class);
    }

    private MarketPrice mapHistoricalPrice(FinancialModelingPrepHistoricalPriceDTO dto) {
        if (dto.getDate() == null) {
            throw new FinancialModelingPrepException("Historical price returned without date");
        }

        MarketPrice marketPrice = new MarketPrice();

        marketPrice.setPriceDate(dto.getDate());
        marketPrice.setOpenPrice(dto.getOpen());
        marketPrice.setHighPrice(dto.getHigh());
        marketPrice.setLowPrice(dto.getLow());
        marketPrice.setClosePrice(dto.getClose());
        marketPrice.setVolume(dto.getVolume());

        return marketPrice;
    }
}
