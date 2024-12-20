package by.tem.service;

import by.tem.dao.ExchangeRateDao;
import by.tem.dto.ExchangeRateDto;
import by.tem.entity.ExchangeRate;
import by.tem.exception.ExchangeRateExistsException;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.mapper.ExchangeRateMapper;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    public List<ExchangeRateDto> findAll() {
        return ExchangeRateMapper.toDtoList(exchangeRateDao.findAll());
    }

    public ExchangeRateDto findByCode(String baseCurrencyCode, String targetCurrencyCode) {
        Optional<ExchangeRate> exchangeOptional = exchangeRateDao.findByCode(baseCurrencyCode, targetCurrencyCode);
        if (exchangeOptional.isPresent()) {
            return ExchangeRateMapper.toDto(exchangeOptional.get());
        }
        throw new ExchangeRateNotFoundException(String.format("Exchange rate not found for base currency code '%s' and target currency code '%s'", baseCurrencyCode, targetCurrencyCode));
    }

    public ExchangeRateDto save(ExchangeRateDto exchangeRateDto) {
        ExchangeRate exchangeRate = ExchangeRateMapper.toExchangeRate(exchangeRateDto);
        Optional<ExchangeRate> exchangeRateOptional = exchangeRateDao.findByCode(
                exchangeRate.getBaseCurrency().getCode(),
                exchangeRate.getTargetCurrency().getCode()
        );
        if (exchangeRateOptional.isEmpty()) {
            ExchangeRate savedExchangeRate = exchangeRateDao.save(exchangeRate);
            return ExchangeRateMapper.toDto(savedExchangeRate);
        }
        throw new ExchangeRateExistsException(
                "Exchange rate with base currency code " + exchangeRateDto.getBaseCurrency().getCode() +
                " and target currency code " + exchangeRateDto.getTargetCurrency().getCode() + " already exist.");
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    private ExchangeRateService() {
    }
}
