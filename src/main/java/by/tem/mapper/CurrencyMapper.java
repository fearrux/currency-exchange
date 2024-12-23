package by.tem.mapper;

import by.tem.dto.CurrencyDto;
import by.tem.entity.Currency;

public class CurrencyMapper {
    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    public CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getName(),
                currency.getCode(),
                currency.getSign());
    }

    public Currency toEntity(CurrencyDto currencyDto) {
        return new Currency(
                currencyDto.id(),
                currencyDto.name(),
                currencyDto.code(),
                currencyDto.sign());
    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }

    private CurrencyMapper() {
    }
}
