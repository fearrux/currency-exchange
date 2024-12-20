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
}
