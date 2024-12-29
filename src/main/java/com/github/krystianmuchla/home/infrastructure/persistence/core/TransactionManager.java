package com.github.krystianmuchla.home.infrastructure.persistence.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    private static final Map<Long, Transaction> TRANSACTIONS = new ConcurrentHashMap<>(1);
    private static final ArrayBlockingQueue<Connection> CONNECTIONS = new ArrayBlockingQueue<>(1);

    static {
        try {
            CONNECTIONS.add(createConnection());
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Transaction createTransaction() throws SQLException {
        var threadId = getThreadId();
        var transaction = TRANSACTIONS.get(threadId);
        if (transaction == null) {
            var connection = takeConnection();
            transaction = new Transaction(connection);
            TRANSACTIONS.put(threadId, transaction);
        } else {
            transaction.nest();
        }
        return transaction;
    }

    public static ReadTransaction createReadTransaction() throws SQLException {
        var transaction = TRANSACTIONS.get(getThreadId());
        Connection connection;
        if (transaction == null) {
            connection = takeConnection();
        } else {
            connection = transaction.connection;
        }
        return new ReadTransaction(connection);
    }

    public static Transaction getTransaction() {
        var transaction = TRANSACTIONS.get(getThreadId());
        assert transaction != null;
        return transaction;
    }

    public static void finishTransaction() throws SQLException {
        var transaction = getTransaction();
        if (transaction.getDepth() < 1) {
            removeTransaction();
            var connection = transaction.connection;
            var result = CONNECTIONS.offer(connection);
            if (!result) {
                connection.close();
            }
        }
    }

    public static void finishReadTransaction(ReadTransaction readTransaction) throws SQLException {
        var transaction = TRANSACTIONS.get(getThreadId());
        if (transaction == null) {
            offerConnection(readTransaction.connection);
        }
    }

    private static void removeTransaction() {
        var transaction = TRANSACTIONS.remove(getThreadId());
        assert transaction != null;
    }

    private static Connection createConnection() throws SQLException {
        var connection = DriverManager.getConnection(ConnectionConfig.URL);
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        return connection;
    }

    private static Connection takeConnection() throws SQLException {
        Connection connection;
        try {
            connection = CONNECTIONS.take();
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
        return maybeRenewConnection(connection);
    }

    private static void offerConnection(Connection connection) throws SQLException {
        var result = CONNECTIONS.offer(connection);
        if (!result) {
            connection.close();
        }
    }

    private static Connection maybeRenewConnection(Connection connection) throws SQLException {
        if (!connection.isValid(1)) {
            connection.close();
            return createConnection();
        }
        return connection;
    }

    private static long getThreadId() {
        return Thread.currentThread().threadId();
    }
}
