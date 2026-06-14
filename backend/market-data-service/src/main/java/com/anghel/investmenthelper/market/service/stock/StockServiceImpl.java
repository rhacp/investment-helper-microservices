package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.client.PredictionClient;
import com.anghel.investmenthelper.market.event.StockCreatedEvent;
import com.anghel.investmenthelper.market.model.dto.TrainingModelRequestDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockTickerResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.SyncStockRequestDTO;
import com.anghel.investmenthelper.market.model.entity.Stock;
import com.anghel.investmenthelper.market.repository.StockRepository;
import com.anghel.investmenthelper.market.service.market_price.MarketPriceService;
import com.anghel.investmenthelper.market.service.yahoo.YahooFinanceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ModelMapper modelMapper;

    private final StockRepository stockRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final YahooFinanceClient yahooFinanceClient;

    private final StockQueryService stockQueryService;

    private final MarketPriceService marketPriceService;

    @Transactional
    @Override
    public StockResponseDTO syncStock(SyncStockRequestDTO request) {
        yahoofinance.Stock yahooStock = yahooFinanceClient.getStock(request.getTicker());

        Stock stock = stockRepository.findStockByTickerIgnoreCase(request.getTicker());
        boolean newStock = stock == null;

        if (newStock) {
            stock = new Stock();
            stock.setTicker(request.getTicker());
        }

        updateStockFromYahoo(stock, yahooStock);
        Stock savedStock = stockRepository.save(stock);
        log.info("Stock synchronized [id={}, ticker={}]",
                savedStock.getId(),
                savedStock.getTicker());

        marketPriceService.syncMarketPrices(savedStock);

        if (newStock) {
            applicationEventPublisher.publishEvent(new StockCreatedEvent(savedStock.getTicker()));
            log.info("Stock created event published [ticker={}]", savedStock.getTicker());
        }

        return modelMapper.map(savedStock, StockResponseDTO.class);
    }

    @Override
    public StockResponseDTO getStockByTicker(String ticker) {
        Stock stock = stockQueryService.getValidStock(ticker);
        log.debug("Stock retrieved [ticker={}]", ticker);

        return modelMapper.map(stock, StockResponseDTO.class);
    }

    @Override
    public List<MarketPriceResponseDTO> getHistoryByTicker(String ticker) {
        Stock stock = stockQueryService.getValidStock(ticker);
        log.debug("Stock retrieved [ticker={}]", ticker);

        return marketPriceService.getMarketPriceListByStock(stock);
    }

    @Override
    public MarketPriceInternalResponseDTO getMarketPriceByTicker(String ticker) {
        Stock stock = stockQueryService.getValidStock(ticker);
        log.debug("Stock retrieved [ticker={}]", ticker);

        return marketPriceService.getMarketPriceByStock(stock);
    }

    @Override
    public void syncAllStocks() {
        List<Stock> stockList = stockRepository.findAll();
        int successful = 0;
        int failed = 0;

        for (Stock stock : stockList) {
            try {
                marketPriceService.syncMarketPrices(stock);
                successful++;
            } catch (Exception e) {
                log.error("Failed to daily sync stock [ticker={}]", stock.getTicker(), e);
                failed++;
            }
        }

        log.info(
                "Daily stock synchronization result [successful={}, failed={}]",
                successful,
                failed
        );
    }

    @Override
    public List<StockTickerResponseDTO> getAllStocks() {
        List<StockTickerResponseDTO> stocks = stockRepository.findAll()
                .stream()
                .map(stock -> new StockTickerResponseDTO(stock.getTicker()))
                .toList();

        log.debug("Retrieved stocks [count={}]", stocks.size());

        return stocks;
    }

    @Override
    public MarketPriceResponseDTO getFullMarketPriceByTicker(String ticker) {
        Stock stock = stockQueryService.getValidStock(ticker);
        log.debug("Stock retrieved [ticker={}]", ticker);

        return marketPriceService.getFullMarketPriceByStock(stock);
    }

    @Override
    public Map<String, MarketPriceInternalResponseDTO> getLatestPrices(List<String> tickers) {
        log.debug("Retrieving latest prices for {} tickers", tickers.size());
        return tickers.stream()
                .collect(Collectors.toMap(Function.identity(), ticker -> getMarketPriceByTicker(ticker)));
    }

    private static void updateStockFromYahoo(Stock stock, yahoofinance.Stock yahooStock) {
        stock.setTicker(yahooStock.getSymbol());
        stock.setCompanyName(yahooStock.getName());
        stock.setCurrency(yahooStock.getCurrency());
        stock.setExchange(yahooStock.getStockExchange());
    }
}
