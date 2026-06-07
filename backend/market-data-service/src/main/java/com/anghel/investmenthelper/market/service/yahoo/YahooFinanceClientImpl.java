package com.anghel.investmenthelper.market.service.yahoo;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class YahooFinanceClientImpl implements YahooFinanceClient {

    @Override
    public Stock getStock(String ticker) {
        try {
            Stock stock = YahooFinance.get(ticker);

            if (stock == null || stock.getQuote() == null) {
                throw new IllegalArgumentException("Stock not found: " +  ticker);
            }

            return stock;
        } catch (IOException exception) {
            throw new IllegalArgumentException("Failed to fetch stock from Yahoo: " + ticker, exception);
        }
    }

    @Override
    public List<HistoricalQuote> getHistory(String ticker, LocalDate startDate, LocalDate endDate) {
        Stock stock = getStock(ticker);

        // check user microservice for ids name in interfaces and controller

        return null;
    }
}
