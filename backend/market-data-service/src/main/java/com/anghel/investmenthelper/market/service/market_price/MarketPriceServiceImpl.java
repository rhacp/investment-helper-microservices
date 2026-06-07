package com.anghel.investmenthelper.market.service.market_price;

import com.anghel.investmenthelper.market.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.entity.MarketPrice;
import com.anghel.investmenthelper.market.model.entity.Stock;
import com.anghel.investmenthelper.market.repository.MarketPriceRepository;
import com.anghel.investmenthelper.market.service.yahoo.YahooFinanceClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import yahoofinance.histquotes.HistoricalQuote;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class MarketPriceServiceImpl implements MarketPriceService {

    private final ModelMapper modelMapper;

    private final MarketPriceRepository marketPriceRepository;

    private final YahooFinanceClient yahooFinanceClient;

    public MarketPriceServiceImpl(ModelMapper modelMapper, MarketPriceRepository marketPriceRepository, YahooFinanceClient yahooFinanceClient) {
        this.modelMapper = modelMapper;
        this.marketPriceRepository = marketPriceRepository;
        this.yahooFinanceClient = yahooFinanceClient;
    }

    @Transactional
    @Override
    public void syncMarketPrices(Stock stock) {
        LocalDate lastDate = marketPriceRepository.findLatestPriceDate(stock);
        LocalDate startDate = lastDate == null ? LocalDate.now().minusYears(2) : lastDate.plusDays(1);

        if (startDate.isAfter(LocalDate.now())) {
            log.debug("No synchronization required [ticker={}]", stock.getTicker());
            return;
        }

        List<HistoricalQuote> historicalQuoteList = yahooFinanceClient.getHistory(
                stock.getTicker(),
                startDate,
                LocalDate.now());

        List<MarketPrice> marketPriceList = historicalQuoteList.stream()
                .filter(historicalQuote -> historicalQuote.getClose() != null && historicalQuote.getDate() != null)
                .map(this::updateMarketPriceFromHistoricalQuote)
                .toList();

        marketPriceList.forEach(marketPrice -> marketPrice.setStock(stock));

        Set<LocalDate> existingPriceDates = marketPriceRepository.findAllPriceDatesByStock(stock);
        marketPriceList = marketPriceList.stream()
                .filter(marketPrice -> !existingPriceDates.contains(marketPrice.getPriceDate()))
                .toList();

        List<MarketPrice> savedMarketPriceList = marketPriceRepository.saveAll(marketPriceList);
        log.info(
                "Market prices synchronized [ticker={}, importedRecords={}]",
                stock.getTicker(),
                savedMarketPriceList.size()
        );
    }

    @Override
    public List<MarketPriceResponseDTO> getMarketPriceListByStock(Stock stock) {
        List<MarketPrice> marketPriceList = marketPriceRepository.findAllByStockOrderByDateAsc(stock);
        log.debug("MarketPrice list retrieved for stock [ticker={}]", stock.getTicker());

        return marketPriceList.stream()
                .map(marketPrice -> modelMapper.map(marketPrice, MarketPriceResponseDTO.class))
                .toList();
    }

    @Override
    public MarketPriceInternalResponseDTO getMarketPriceByStock(Stock stock) {
        MarketPrice marketPrice = marketPriceRepository.findTopByStockOrderByDateDesc(stock)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Market Price not found for stock " + stock.getTicker()));

        log.debug("MarketPrice retrieved for ticker [ticker={}]", stock.getTicker());

        return new MarketPriceInternalResponseDTO(
                marketPrice.getStock().getTicker(),
                marketPrice.getClosePrice());
    }

    private MarketPrice updateMarketPriceFromHistoricalQuote(HistoricalQuote historicalQuote) {
        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setClosePrice(historicalQuote.getClose());
        marketPrice.setHighPrice(historicalQuote.getHigh());
        marketPrice.setLowPrice(historicalQuote.getLow());
        marketPrice.setOpenPrice(historicalQuote.getOpen());
        marketPrice.setVolume(historicalQuote.getVolume());
        marketPrice.setPriceDate(historicalQuote.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate());

        return marketPrice;
    }
}
