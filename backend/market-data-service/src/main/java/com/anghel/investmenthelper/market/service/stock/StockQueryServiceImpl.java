package com.anghel.investmenthelper.market.service.stock;

import com.anghel.investmenthelper.market.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.market.model.entity.Stock;
import com.anghel.investmenthelper.market.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockQueryServiceImpl implements StockQueryService {

    private final StockRepository stockRepository;

    public StockQueryServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public Stock getValidStock(String ticker) {
        Stock stock = stockRepository.findStockByTickerIgnoreCase(ticker);

        if (stock == null) {
            throw new ResourceNotFoundException("Stock not found " + ticker);
        }

        return stock;
    }
}
