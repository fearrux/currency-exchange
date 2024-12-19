package by.tem.dao;

import by.tem.entity.Currency;
import by.tem.exception.CurrencyExistsException;
import by.tem.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   code,
                   full_name,
                   sign
            FROM currencies
            """;
    private static final String FIND_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO currencies(code, full_name, sign)
            VALUES (?, ?, ?)
            """;

    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = buildCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency save(Currency currency) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt(1));
            }
            return currency;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign"));
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private CurrencyDao() {
    }

    public static void main(String[] args) {
        CurrencyDao instance = CurrencyDao.getInstance();
        for (Currency c : instance.findAll()) {
            System.out.println(c);
        }

    }
}
