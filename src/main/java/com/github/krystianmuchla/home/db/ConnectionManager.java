package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionManager {
    private static Connection commonConnection;
    private static Map<Long, Connection> registeredConnections = new HashMap<>();

    public static Connection get() {
        final var threadId = Thread.currentThread().threadId();
        final var registeredConnection = registeredConnections.get(threadId);
        if (registeredConnection != null) return registeredConnection;
        if (commonConnection == null) commonConnection = create();
        return commonConnection;
    }

    @SneakyThrows
    public static void register() {
        final var threadId = Thread.currentThread().threadId();
        final var looseConnection = registeredConnections.put(threadId, create());
        if (looseConnection != null) looseConnection.close();
    }

    @SneakyThrows
    public static Connection create() {
        final var connection = DriverManager.getConnection(
            DbConfig.URL,
            DbConfig.USER,
            DbConfig.PASSWORD
        );
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }
}
