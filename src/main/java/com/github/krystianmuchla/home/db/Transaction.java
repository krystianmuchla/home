package com.github.krystianmuchla.home.db;

import java.sql.SQLException;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {
    public static void run(final Runnable runnable) throws SQLException {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T run(final Supplier<T> supplier) throws SQLException {
        final var connection = ConnectionManager.get();
        try {
            final var result = supplier.get();
            connection.commit();
            return result;
        } catch (final Exception exception) {
            connection.rollback();
            throw new RuntimeException(exception);
        }
    }
}
