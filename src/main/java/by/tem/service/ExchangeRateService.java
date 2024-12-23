package by.tem.service;

import by.tem.dao.CurrencyDao;
import by.tem.dto.CurrencyDto;
import by.tem.dto.CurrencyExchangeDto;
import by.tem.dto.ExchangeRateDto;
import by.tem.dao.ExchangeRateDao;
import by.tem.entity.Currency;
import by.tem.entity.ExchangeRate;
import by.tem.exception.CurrencyNotFoundException;
import by.tem.exception.ExchangeRateExistsException;
import by.tem.exception.ExchangeRateNotFoundException;
import by.tem.exception.InvalidDataException;
import by.tem.mapper.ExchangeRateMapper;
import by.tem.validation.CurrencyValidator;
import by.tem.validation.ExchangeRateValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private static final ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRateValidator exchangeRateValidator = new ExchangeRateValidator();
    private final CurrencyValidator currencyValidator = new CurrencyValidator();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    public List<ExchangeRateDto> findAll() {
        return exchangeRateDao.findAll().stream().map(exchangeRateMapper::toDto).toList();
    }

    public ExchangeRateDto findByCodes(String codes) {
        if (!exchangeRateValidator.isValidExchangeRate(codes)) {
            throw new InvalidDataException("Exchange rate is incorrect.");
        }

        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, 6);

        ExchangeRate exchangeOptional = exchangeRateDao.findByCode(baseCurrencyCode, targetCurrencyCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException(String.format("Exchange rate not found for base currency code '%s' and target currency code '%s'", baseCurrencyCode, targetCurrencyCode)));

        return exchangeRateMapper.toDto(exchangeOptional);
    }

    public ExchangeRateDto save(String baseCurrencyCode, String targetCurrencyCode, String rate) {
        if (!currencyValidator.isValidCode(baseCurrencyCode)) {
            throw new InvalidDataException("Base currency code is incorrect.");
        }
        if (!currencyValidator.isValidCode(targetCurrencyCode)) {
            throw new InvalidDataException("Target currency code is incorrect.");
        }
        if (!exchangeRateValidator.isValidNumber(rate)) {
            throw new InvalidDataException("Rate is incorrect.");
        }

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Base currency not found in database."));
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency codes are not available in database."));
        BigDecimal rateValue = new BigDecimal(rate);

        Optional<ExchangeRate> exchangeOptional = exchangeRateDao.findByCode(baseCurrencyCode, targetCurrencyCode);
        exchangeOptional.ifPresent(exchangeRate -> {
            throw new ExchangeRateExistsException("Exchange rate exists.");
        });

        ExchangeRate exchangeRate = new ExchangeRate(null, baseCurrency, targetCurrency, rateValue);
        ExchangeRate saved = exchangeRateDao.save(exchangeRate);

        return ExchangeRateMapper.getInstance().toDto(saved);
    }

    public ExchangeRateDto update(String codes, String rate) {
        if (!exchangeRateValidator.isValidExchangeRate(codes)) {
            throw new InvalidDataException("Exchange rate is incorrect.");
        }
        if (!exchangeRateValidator.isValidNumber(rate)) {
            throw new InvalidDataException("Exchange rate is incorrect.");
        }

        String baseCurrencyCode = codes.substring(0, 3);
        String targetCurrencyCode = codes.substring(3, 6);
        BigDecimal rateValue = new BigDecimal(rate);

        boolean isUpdate = exchangeRateDao.update(baseCurrencyCode, targetCurrencyCode, rateValue);
        if (isUpdate) {
            return exchangeRateMapper.toDto(exchangeRateDao.findByCode(baseCurrencyCode, targetCurrencyCode).get());
        }
        throw new ExchangeRateNotFoundException("Exchange rate code '" + codes + "' not found.");
    }

    public CurrencyExchangeDto exchange(String from, String to, String amount) {
        if (!currencyValidator.isValidCode(from)) {
            throw new InvalidDataException("Code: " + from + " is incorrect.");
        }
        if (!currencyValidator.isValidCode(to)) {
            throw new InvalidDataException("Code: " + from + " is incorrect.");
        }
        if (!exchangeRateValidator.isValidNumber(amount)) {
            throw new InvalidDataException("Amount: " + amount + " + is incorrect.");
        }

        // direct
        BigDecimal amountValue = new BigDecimal(amount);
        Optional<ExchangeRate> exchangeRate = exchangeRateDao.findByCode(from, to);

        if (exchangeRate.isPresent()) {
            return buildCurrencyExchangeDto(exchangeRate.get(), amountValue);
        }

        // reverse
        Optional<ExchangeRate> reverseExchangeRate = exchangeRateDao.findByCode(to, from);

        if (reverseExchangeRate.isPresent()) {
            BigDecimal rate =  BigDecimal.ONE.divide(reverseExchangeRate.get().getRate(), 6, RoundingMode.HALF_UP);
            ExchangeRate resultExchangeRate = new ExchangeRate(
                    null,
                    reverseExchangeRate.get().getTargetCurrency(),
                    reverseExchangeRate.get().getBaseCurrency(),
                    rate
            );
            return buildCurrencyExchangeDto(resultExchangeRate, amountValue);
        }

        // cross
        CurrencyDto currencyUsd = currencyService.findByCode("USD");
        Optional<ExchangeRate> usdBaseRate = exchangeRateDao.findByCode(currencyUsd.code(), from);
        Optional<ExchangeRate> usdTargetRate = exchangeRateDao.findByCode(currencyUsd.code(), to);

        if (usdBaseRate.isPresent() && usdTargetRate.isPresent()) {
            BigDecimal crossRate = usdBaseRate.get().getRate().divide(usdTargetRate.get().getRate(), 6, RoundingMode.HALF_UP);
            ExchangeRate resultExchangeRate = new ExchangeRate(
                    null,
                    usdBaseRate.get().getTargetCurrency(),
                    usdTargetRate.get().getTargetCurrency(),
                    crossRate
            );
            return buildCurrencyExchangeDto(resultExchangeRate, amountValue);
        }
        throw new ExchangeRateNotFoundException("For this currency (%s, %s), the pair not found a rate".formatted(from, to));
    }

    public static ExchangeRateService getInstance() {
        return INSTANCE;
    }

    private CurrencyExchangeDto buildCurrencyExchangeDto(ExchangeRate exchangeRate, BigDecimal amount) {
        return new CurrencyExchangeDto(
                exchangeRate.getBaseCurrency(),
                exchangeRate.getTargetCurrency(),
                exchangeRate.getRate(),
                amount,
                getConvertedAmount(exchangeRate.getRate(), amount)
        );
    }

    private BigDecimal getConvertedAmount(BigDecimal rate, BigDecimal amount) {
        return rate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private ExchangeRateService() {
    }
}
