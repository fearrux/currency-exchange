package by.tem.util;

import by.tem.exception.DatabaseConnectionException;
import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPoolService {
    private static final BasicDataSource dataSource = new BasicDataSource();
    private static final String DRIVER_CLASS_NAME_KEY = "db.driver-class-name";
    private static final String URL_KEY = "db.url";
    private static final int INITIAL_SIZE = 10;
    private static final int MIN_IDLE = 5;
    private static final int MAX_IDLE = 8;
    private static final int MAX_OPEN_PREPARED_STATEMENT = 20;

    static {
        String driverClassName = PropertiesUtil.get(DRIVER_CLASS_NAME_KEY);
        String url = PropertiesUtil.get(URL_KEY);

        if (driverClassName == null || url == null) {
            throw new IllegalArgumentException("Database configuration is missing.");
        }

        dataSource.setDriverClassName(PropertiesUtil.get(DRIVER_CLASS_NAME_KEY));
        dataSource.setUrl(PropertiesUtil.get(URL_KEY));
        dataSource.setInitialSize(INITIAL_SIZE);
        dataSource.setMaxIdle(MAX_IDLE);
        dataSource.setMinIdle(MIN_IDLE);
        dataSource.setMaxOpenPreparedStatements(MAX_OPEN_PREPARED_STATEMENT);
    }


    public static Connection get() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error getting connection from pool");
        }
    }

    public static void close() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Error getting connection from pool");
        }
    }

    private ConnectionPoolService() {
    }
}