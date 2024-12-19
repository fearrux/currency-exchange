package by.tem.exception;

public class CurrencyExistsException extends RuntimeException {
    public CurrencyExistsException(String message) {
        super(message);
    }
}
