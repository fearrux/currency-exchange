package by.tem.validation;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ExchangeRateValidator {
    private static final int EXCHANGE_RATE_LENGTH = 6;
    private static final Pattern VALID_PATTERN_EXCHANGE_RATE = Pattern.compile("[a-zA-Z]+");
    private static final Pattern VALID_PATTERN_NUMBER = Pattern.compile("^(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?$");

    public boolean isValidExchangeRate(String exchangeRate) {
        return exchangeRate != null
               && exchangeRate.length() == EXCHANGE_RATE_LENGTH
               && VALID_PATTERN_EXCHANGE_RATE.matcher(exchangeRate).matches();
    }

    public boolean isValidNumber(String number) {
        return number != null
               && VALID_PATTERN_NUMBER.matcher(number).matches()
               && new BigDecimal(number).compareTo(BigDecimal.ZERO) > 0;
    }
}
