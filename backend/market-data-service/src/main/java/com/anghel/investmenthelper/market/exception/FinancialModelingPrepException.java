package com.anghel.investmenthelper.market.exception;

public class FinancialModelingPrepException extends RuntimeException {
    public FinancialModelingPrepException(String message) {
        super(message);
    }

    public FinancialModelingPrepException(String message, Throwable cause) {
        super(message, cause);
    }
}
