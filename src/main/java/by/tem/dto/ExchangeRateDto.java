package by.tem.dto;

import by.tem.entity.Currency;

import java.math.BigDecimal;

public record ExchangeRateDto(Integer id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
}