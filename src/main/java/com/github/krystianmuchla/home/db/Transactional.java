package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

public interface Transactional {
    default void transactional(final Connection dbConnection, final Runnable runnable) throws SQLException {
        transactional(dbConnection, () -> {
            runnable.run();
            return null;
        });
    }

    default <T> T transactional(final Connection dbConnection, final Supplier<T> supplier) throws SQLException {
        try {
            final T result = supplier.get();
            dbConnection.commit();
            return result;
        } catch (final Exception exception) {
            dbConnection.rollback();
            throw new RuntimeException(exception);
        }
    }
}
