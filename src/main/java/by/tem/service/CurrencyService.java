package by.tem.service;

import by.tem.dao.CurrencyDao;
import by.tem.dto.CurrencyDto;
import by.tem.entity.Currency;
import by.tem.exception.CurrencyExistsException;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.mapper.CurrencyMapper;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public List<CurrencyDto> findAll() {
        return CurrencyMapper.toDtoList(currencyDao.findAll());
    }

    public CurrencyDto findByCode(String code) {
        Optional<Currency> currencyOptional = currencyDao.findByCode(code);
        if (currencyOptional.isPresent()) {
            return CurrencyMapper.toDto(currencyOptional.get());
        }
        throw new CurrencyNotFoundException("Currency with code " + code + " not found.");
    }

    public CurrencyDto save(CurrencyDto currencyDto) {
        Currency currency = CurrencyMapper.toCurrency(currencyDto);
        Optional<Currency> currencyOptional = currencyDao.findByCode(currency.getCode());
        if (currencyOptional.isEmpty()) {
            Currency savedCurrency = currencyDao.save(currency);
            return CurrencyMapper.toDto(savedCurrency);
        }
        throw new CurrencyExistsException("Currency with code " + currency.getCode() + " already exists.");
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    private CurrencyService() {
    }
}
