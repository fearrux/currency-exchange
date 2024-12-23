package by.tem.dao;

import by.tem.entity.Currency;
import by.tem.entity.ExchangeRate;
import by.tem.exception.DatabaseConnectionException;
import by.tem.util.ConnectionPoolService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private static final String FIND_ALL_SQL = """
            SELECT er.id,
                   er.base_currency_id,
                   er.target_currency_id,
                   er.rate,
                   base_currency.full_name AS base_full_name,
                   base_currency.code AS base_code,
                   base_currency.sign AS base_sign,
                   target_currency.full_name AS target_full_name,
                   target_currency.code AS target_code,
                   target_currency.sign AS target_sign
            FROM exchange_rates er
            JOIN main.currencies base_currency on er.base_currency_id = base_currency.id
            JOIN main.currencies target_currency on er.target_currency_id = target_currency.id
            """;
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE base_code = ? AND target_code = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?) AND
                target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionPoolService.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Something went wrong when you tried to connect database.");
        }
    }

    public Optional<ExchangeRate> findByCode(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = ConnectionPoolService.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);
            ResultSet resultSet = preparedStatement.executeQuery();
            ExchangeRate exchangeRate = null;
            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Something went wrong when you tried to connect database.");
        }
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionPoolService.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                exchangeRate.setId(generatedKeys.getInt(1));
            }
            return exchangeRate;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Something went wrong when you tried to connect database.");
        }
    }

    public boolean update(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        try (Connection connection = ConnectionPoolService.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Something went wrong when you tried to connect database.");
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                new Currency(
                        resultSet.getInt("base_currency_id"),
                        resultSet.getString("base_full_name"),
                        resultSet.getString("base_code"),
                        resultSet.getString("base_sign")),
                new Currency(
                        resultSet.getInt("target_currency_id"),
                        resultSet.getString("target_full_name"),
                        resultSet.getString("target_code"),
                        resultSet.getString("target_sign")),
                resultSet.getBigDecimal("rate")
        );
    }

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    private ExchangeRateDao() {
    }
}
