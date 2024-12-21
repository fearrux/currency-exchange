package by.tem.validation;

import by.tem.exception.InvalidDataException;

public class ExchangeRateValidator {
    public static final int EXCHANGE_RATE_LENGTH = 6;

    public static boolean isValidExchangeRate(String exchangeRate) {
        if (exchangeRate == null) {
            throw new InvalidDataException("Exchange rate cannot be null.");
        }
        if (exchangeRate.length() != EXCHANGE_RATE_LENGTH) {
            throw new InvalidDataException("Exchange rate is incorrect.");
        }
        if (!exchangeRate.matches("[a-zA-Z]+")) {
            throw new InvalidDataException("Exchange rate must consist only of English letters.");
        }
        return true;
    }

    public static boolean isValidRate(String rate) {
        if (rate == null) {
            throw new InvalidDataException("A rate cannot be null");
        }
        if (!rate.matches("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$")) {
            throw new InvalidDataException("A rate must be number.");
        }
        if (Double.parseDouble(rate) <= 0) {
            throw new InvalidDataException("A rate must be positive number and not equal zero.");
        }
        return true;
    }
}
