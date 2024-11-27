package com.github.krystianmuchla.home.infrastructure.persistence.core;

import com.github.krystianmuchla.home.application.exception.InternalException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private static final Map<Long, Connection> WRITE_CONNECTIONS = new ConcurrentHashMap<>();
    private static final ArrayBlockingQueue<Connection> CONNECTIONS = new ArrayBlockingQueue<>(1);

    static {
        try {
            CONNECTIONS.add(createConnection());
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }

    public static WriteConnection addWriteConnection() throws SQLException {
        var connection = takeConnection();
        connection = maybeRenewConnection(connection);
        WRITE_CONNECTIONS.put(threadId(), connection);
        return new WriteConnection(connection);
    }

    public static Connection getWriteConnection() {
        var connection = WRITE_CONNECTIONS.get(threadId());
        assert connection != null;
        return connection;
    }

    public static ReadConnection getReadConnection() throws SQLException {
        var connection = WRITE_CONNECTIONS.get(threadId());
        if (connection == null) {
            connection = takeConnection();
            connection = maybeRenewConnection(connection);
        }
        return new ReadConnection(connection);
    }

    public static void returnWriteConnection() throws SQLException {
        var connection = WRITE_CONNECTIONS.remove(threadId());
        assert connection != null;
        var result = CONNECTIONS.offer(connection);
        if (!result) {
            connection.close();
        }
    }

    public static void returnReadConnection(Connection connection) throws SQLException {
        if (!WRITE_CONNECTIONS.containsKey(threadId())) {
            var result = CONNECTIONS.offer(connection);
            if (!result) {
                connection.close();
            }
        }
    }

    private static Connection createConnection() throws SQLException {
        var connection = DriverManager.getConnection(ConnectionConfig.URL);
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        return connection;
    }

    private static Connection takeConnection() {
        try {
            return CONNECTIONS.take();
        } catch (InterruptedException exception) {
            throw new InternalException(exception);
        }
    }

    private static Connection maybeRenewConnection(Connection connection) throws SQLException {
        if (!connection.isValid(1)) {
            connection.close();
            return createConnection();
        }
        return connection;
    }

    private static long threadId() {
        return Thread.currentThread().threadId();
    }
}
