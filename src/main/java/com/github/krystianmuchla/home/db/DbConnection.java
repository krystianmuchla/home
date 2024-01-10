package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static Connection instance;

    private DbConnection() {
    }

    public static Connection getInstance() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }

    public static Connection create() {
        final Connection dbConnection;
        try {
            dbConnection = DriverManager.getConnection(
                DbConfig.URL,
                DbConfig.USER,
                DbConfig.PASSWORD
            );
            dbConnection.setAutoCommit(false);
            dbConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (final SQLException exception) {
            throw new RuntimeException(exception);
        }
        return dbConnection;
    }
}
