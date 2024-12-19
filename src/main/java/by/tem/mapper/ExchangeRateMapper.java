package by.tem.mapper;

import by.tem.dto.ExchangeRateDto;
import by.tem.entity.ExchangeRate;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeRateMapper {
    public static ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );
    }

    public static ExchangeRate toExchangeRate(ExchangeRateDto exchangeRateDto) {
        return new ExchangeRate(
                exchangeRateDto.getId(),
                exchangeRateDto.getBaseCurrency(),
                exchangeRateDto.getTargetCurrency(),
                exchangeRateDto.getRate()
        );
    }

    public static List<ExchangeRateDto> toDtoList(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .map(ExchangeRateMapper::toDto)
                .collect(Collectors.toList());
    }
}
