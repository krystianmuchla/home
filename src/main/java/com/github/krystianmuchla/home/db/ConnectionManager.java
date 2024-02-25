package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

public class ConnectionManager {
    private static Connection commonConnection;
    private static Map<Long, Connection> registeredConnections;

    static {
        registeredConnections = Collections.synchronizedMap(new HashMap<>());
    }

    public static Connection getConnection() {
        final var threadId = Thread.currentThread().threadId();
        final var registeredConnection = registeredConnections.get(threadId);
        if (registeredConnection != null) {
            return registeredConnection;
        }
        if (commonConnection == null) {
            commonConnection = createConnection();
        }
        return commonConnection;
    }

    @SneakyThrows
    public static void registerConnection() {
        final var threadId = Thread.currentThread().threadId();
        final var looseConnection = registeredConnections.put(threadId, createConnection());
        if (looseConnection != null) {
            looseConnection.close();
        }
    }

    @SneakyThrows
    public static Connection createConnection() {
        final var connection = DriverManager.getConnection(
                ConnectionConfig.URL,
                ConnectionConfig.USER,
                ConnectionConfig.PASSWORD);
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }
}
