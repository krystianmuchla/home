package com.github.krystianmuchla.home.infrastructure.persistence.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public class Transaction implements AutoCloseable {
    public final Connection connection;
    private int depth;

    public Transaction(Connection connection) {
        this.connection = connection;
        depth = 1;
    }

    public int getDepth() {
        return depth;
    }

    public void nest() {
        depth++;
    }

    public void decrementDepth() {
        depth--;
        assert depth >= 0;
    }

    @Override
    public void close() throws SQLException {
        decrementDepth();
        TransactionManager.finishTransaction();
    }

    public static void run(Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T run(Supplier<T> supplier) {
        try (var transaction = TransactionManager.createTransaction()) {
            T result;
            try {
                result = supplier.get();
            } catch (Exception exception) {
                transaction.connection.rollback();
                throw new TransactionException(exception);
            }
            transaction.connection.commit();
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
