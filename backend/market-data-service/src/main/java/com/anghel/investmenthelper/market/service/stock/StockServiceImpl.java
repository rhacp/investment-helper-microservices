package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceInternalResponseDTO;
import com.anghel.investmenthelper.market.model.dto.market_price.MarketPriceResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.StockResponseDTO;
import com.anghel.investmenthelper.market.model.dto.stock.SyncStockRequestDTO;
import com.anghel.investmenthelper.market.model.entity.Stock;
import com.anghel.investmenthelper.market.repository.StockRepository;
import com.anghel.investmenthelper.market.service.market_price.MarketPriceService;
import com.anghel.investmenthelper.market.service.yahoo.YahooFinanceClient;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    private final ModelMapper modelMapper;

    private final StockRepository stockRepository;

    private final YahooFinanceClient yahooFinanceClient;

    private final StockQueryService stockQueryService;

    private final MarketPriceService marketPriceService;

    public StockServiceImpl(ModelMapper modelMapper,
                            StockRepository stockRepository,
                            YahooFinanceClient yahooFinanceClient,
                            StockQueryService stockQueryService,
                            MarketPriceService marketPriceService) {
        this.modelMapper = modelMapper;
        this.stockRepository = stockRepository;
        this.yahooFinanceClient = yahooFinanceClient;
        this.stockQueryService = stockQueryService;
        this.marketPriceService = marketPriceService;
    }

    @Transactional
    @Override
    public StockResponseDTO syncStock(SyncStockRequestDTO request) {
        yahoofinance.Stock yahooStock = yahooFinanceClient.getStock(request.getTicker());

        Stock stock = stockRepository.findStockByTickerIgnoreCase(request.getTicker());

        if (stock == null) {
            stock = new Stock();
            stock.setTicker(request.getTicker());
        }

        updateStockFromYahoo(stock, yahooStock);
        Stock savedStock = stockRepository.save(stock);
        log.info("Stock synchronized [id={}, ticker={}]",
                savedStock.getId(),
                savedStock.getTicker());

        marketPriceService.syncMarketPrices(savedStock);

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

    private static void updateStockFromYahoo(Stock stock, yahoofinance.Stock yahooStock) {
        stock.setTicker(yahooStock.getSymbol());
        stock.setCompanyName(yahooStock.getName());
        stock.setCurrency(yahooStock.getCurrency());
        stock.setExchange(yahooStock.getStockExchange());
    }
}
