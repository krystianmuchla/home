package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.error.exception.TransactionException;
import lombok.SneakyThrows;

import java.util.function.Supplier;

public class Transaction {
    public static void run(final Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    @SneakyThrows
    public static <T> T run(final Supplier<T> supplier) {
        final var connection = ConnectionManager.getConnection();
        try {
            final var result = supplier.get();
            connection.commit();
            return result;
        } catch (final Exception exception) {
            connection.rollback();
            throw new TransactionException(exception);
        }
    }
}
