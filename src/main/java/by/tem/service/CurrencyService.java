package by.tem.service;

import by.tem.dao.CurrencyDao;
import by.tem.dto.CurrencyDto;
import by.tem.entity.Currency;
import by.tem.exception.CurrencyExistsException;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.mapper.CurrencyMapper;
import by.tem.validation.CurrencyValidator;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    public List<CurrencyDto> findAll() {
        return currencyDao.findAll().stream().map(currencyMapper::toDto).toList();
    }

    public CurrencyDto findByCode(String code) {
        if (!currencyValidator.isValidCode(code)) {
            throw new InvalidDataException("Code is incorrect.");
        }

        Optional<Currency> currencyOptional = currencyDao.findByCode(code);
        Currency currency = currencyOptional.orElseThrow(() -> new CurrencyNotFoundException("Currency with " + code + " not found."));

        return currencyMapper.toDto(currency);
    }

    public CurrencyDto save(CurrencyDto currencyDto) {
        if (!currencyValidator.isValidName(currencyDto.name())) {
            throw new InvalidDataException("Name is incorrect.");
        }
        if (!currencyValidator.isValidCode(currencyDto.code())) {
            throw new InvalidDataException("Code is incorrect");
        }
        if (!currencyValidator.isValidSign(currencyDto.sign())) {
            throw new InvalidDataException("Sign is incorrect.");
        }

        Currency currency = currencyMapper.toEntity(currencyDto);
        Optional<Currency> currencyOptional = currencyDao.findByCode(currency.getCode());
        if (currencyOptional.isPresent()) {
            throw new CurrencyExistsException("Currency with code " + currency.getCode() + " already exists.");
        }

        Currency savedCurrency = currencyDao.save(currency);
        return currencyMapper.toDto(savedCurrency);
    }

    public static CurrencyService getInstance() {
        return INSTANCE;
    }

    private CurrencyService() {
    }
}
