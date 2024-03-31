package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    private static Connection commonConnection;
    private static final Map<Long, Connection> REGISTERED_CONNECTIONS;

    static {
        REGISTERED_CONNECTIONS = Collections.synchronizedMap(new HashMap<>());
    }

    public static Connection getConnection() throws SQLException {
        final var threadId = Thread.currentThread().threadId();
        final var registeredConnection = REGISTERED_CONNECTIONS.get(threadId);
        if (registeredConnection != null) {
            return registeredConnection;
        }
        if (commonConnection == null) {
            commonConnection = createConnection();
        }
        return commonConnection;
    }

    public static void registerConnection() throws SQLException {
        final var threadId = Thread.currentThread().threadId();
        final var looseConnection = REGISTERED_CONNECTIONS.put(threadId, createConnection());
        if (looseConnection != null) {
            looseConnection.close();
        }
    }

    public static Connection createConnection() throws SQLException {
        final var connection = DriverManager.getConnection(
            ConnectionConfig.URL,
            ConnectionConfig.USER,
            ConnectionConfig.PASSWORD);
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }
}
