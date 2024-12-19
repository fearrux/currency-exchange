package by.tem.mapper;

import by.tem.dto.CurrencyDto;
import by.tem.entity.Currency;

import java.util.List;
import java.util.stream.Collectors;

public class CurrencyMapper {
    public static CurrencyDto toDto(Currency currency) {
        return new CurrencyDto(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public static Currency toCurrency(CurrencyDto dto) {
        Currency currency = new Currency();
        currency.setId(dto.getId());
        currency.setFullName(dto.getName());
        currency.setCode(dto.getCode());
        currency.setSign(dto.getSign());
        return currency;
    }

    public static List<CurrencyDto> toDtoList(List<Currency> currencies) {
        return currencies.stream()
                .map(CurrencyMapper::toDto)
                .collect(Collectors.toList());
    }
}
