package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.error.exception.TransactionException;

import java.sql.SQLException;
import java.util.function.Supplier;

public class Transaction {
    public static void run(final Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T run(final Supplier<T> supplier) {
        try {
            final var connection = ConnectionManager.getConnection();
            try {
                final var result = supplier.get();
                connection.commit();
                return result;
            } catch (final Exception exception) {
                connection.rollback();
                throw new TransactionException(exception);
            }
        } catch (final SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
