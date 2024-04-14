package com.github.krystianmuchla.home.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionManager.class);
    private static final Map<Long, Connection> REGISTERED_CONNECTIONS = new ConcurrentHashMap<>();
    private static final ArrayBlockingQueue<Connection> CONNECTIONS = new ArrayBlockingQueue<>(ConnectionConfig.POOL_SIZE);

    public static BorrowedConnection borrowConnection() throws SQLException {
        final var connection = REGISTERED_CONNECTIONS.getOrDefault(threadId(), pollConnection());
        return new BorrowedConnection(connection);
    }

    public static void returnConnection(final Connection connection) throws SQLException {
        if (REGISTERED_CONNECTIONS.containsValue(connection)) {
            // no-op
        } else {
            offerConnection(connection);
        }
    }

    public static RegisteredConnection registerConnection() throws SQLException {
        final var connection = pollConnection();
        final var previousConnection = REGISTERED_CONNECTIONS.put(threadId(), connection);
        if (previousConnection != null) {
            previousConnection.close();
            LOG.warn("Several connection registration occurred");
        }
        return new RegisteredConnection(connection);
    }

    public static void deregisterConnection(final Connection connection) throws SQLException {
        final var result = REGISTERED_CONNECTIONS.values().remove(connection);
        if (!result) {
            LOG.warn("Missing connection to deregister");
            return;
        }
        offerConnection(connection);
    }

    private static Connection pollConnection() throws SQLException {
        var connection = CONNECTIONS.poll();
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }

    private static void offerConnection(final Connection connection) throws SQLException {
        final var result = CONNECTIONS.offer(connection);
        if (!result) {
            connection.close();
        }
    }

    private static Connection createConnection() throws SQLException {
        final var connection = DriverManager.getConnection(
            ConnectionConfig.URL,
            ConnectionConfig.USER,
            ConnectionConfig.PASSWORD);
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return connection;
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }
}
