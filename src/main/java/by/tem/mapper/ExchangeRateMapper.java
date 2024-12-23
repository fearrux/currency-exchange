package by.tem.mapper;

import by.tem.dto.ExchangeRateDto;
import by.tem.entity.ExchangeRate;

public class ExchangeRateMapper {
    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();

    public ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDto(
                exchangeRate.getId(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate()
        );
    }

    public static ExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    private ExchangeRateMapper(){
    }
}
