package by.tem.dto;

import by.tem.entity.Currency;

import java.math.BigDecimal;

public record CurrencyExchangeDto(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
