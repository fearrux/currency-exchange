package by.tem.service;

import by.tem.dao.CurrencyDao;
import by.tem.dto.CurrencyDto;
import by.tem.entity.Currency;
import by.tem.exception.CurrencyExistsException;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.mapper.CurrencyMapper;
import by.tem.validation.CurrencyValidator;

import java.util.ArrayList;
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
        List<String> errorMessages = new ArrayList<>();

        if (!currencyValidator.isValidName(currencyDto.name())) {
            errorMessages.add("The currency name must be no more than 30 characters and no less than 2, and must be in English letters only.");
        }
        if (!currencyValidator.isValidCode(currencyDto.code())) {
            errorMessages.add("The currency code must contain only English letters and be 3 long.");
        }
        if (!currencyValidator.isValidSign(currencyDto.sign())) {
            errorMessages.add("The currency sign must be no more than 3 characters and consist of only English letters.");
        }

        if (!errorMessages.isEmpty()) {
            throw new InvalidDataException(String.join(" ", errorMessages));
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
