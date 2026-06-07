package com.anghel.investmenthelper.market.service.yahoo;

import com.anghel.investmenthelper.market.exception.ResourceNotFoundException;
import com.anghel.investmenthelper.market.exception.YahooFinanceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@Slf4j
@Service
public class YahooFinanceClientImpl implements YahooFinanceClient {

    @Override
    public Stock getStock(String ticker) {
        try {
            Stock stock = YahooFinance.get(ticker);
            log.debug("Fetched stock from Yahoo Finance [ticker={}]", ticker);


            if (stock == null || stock.getQuote() == null || stock.getName() == null) {
                throw new ResourceNotFoundException("Stock not found: " +  ticker);
            }

            return stock;
        } catch (IOException exception) {
            log.error(
                    "Failed to fetch stock from Yahoo Finance [ticker={}]",
                    ticker,
                    exception
            );
            throw new YahooFinanceException("Failed to fetch stock from Yahoo: " + ticker, exception);
        }
    }

    @Override
    public List<HistoricalQuote> getHistory(String ticker, LocalDate startDate, LocalDate endDate) {
        try {
            Stock stock = getStock(ticker);

            Calendar from = Calendar.getInstance();
            from.setTime(java.sql.Date.valueOf(startDate));

            Calendar to = Calendar.getInstance();
            to.setTime(java.sql.Date.valueOf(endDate));


            List<HistoricalQuote> historicalQuoteList = stock.getHistory(from, to, Interval.DAILY);
            log.debug(
                    "Fetched historical quotes from Yahoo Finance [ticker={}, records={}]",
                    ticker,
                    historicalQuoteList.size()
            );

            return historicalQuoteList;
        } catch (IOException exception) {
            log.error(
                    "Failed to fetch history quote from Yahoo Finance [ticker={}]",
                    ticker,
                    exception
            );
            throw new YahooFinanceException("Failed to fetch historical quotes from Yahoo: " + ticker, exception);
        }
    }
}
