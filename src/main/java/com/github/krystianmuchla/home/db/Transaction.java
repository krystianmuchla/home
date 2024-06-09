package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.TransactionException;

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
        try (final var registeredConnection = ConnectionManager.registerConnection()) {
            final var connection = registeredConnection.connection();
            final T result;
            try {
                result = supplier.get();
            } catch (final Exception exception) {
                connection.rollback();
                throw new TransactionException(exception);
            }
            connection.commit();
            return result;
        } catch (final SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
