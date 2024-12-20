package by.tem.service;

import by.tem.dao.ExchangeRateDao;
import by.tem.dto.ExchangeRateDto;
import by.tem.entity.ExchangeRate;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.mapper.ExchangeRateMapper;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

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

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    private ExchangeRateService() {
    }
}
