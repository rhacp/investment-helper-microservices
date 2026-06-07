package com.anghel.investmenthelper.market.exception;

public class YahooFinanceException extends RuntimeException {
    public YahooFinanceException(String message, Exception cause) {
        super(message);
    }
}
