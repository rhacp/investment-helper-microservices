package com.anghel.investmenthelper.market.service.yahoo;

import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

import java.time.LocalDate;
import java.util.List;

public interface YahooFinanceClient {

    Stock getStock(String ticker);

    List<HistoricalQuote> getHistory(String ticker, LocalDate startDate, LocalDate endDate);
}
