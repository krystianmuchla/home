package com.github.krystianmuchla.home.infrastructure.persistence.core;

import java.sql.SQLException;
import java.util.function.Supplier;

// todo
public class Transaction {
    public static void run(Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T run(Supplier<T> supplier) {
        try (var writeConnection = ConnectionManager.addWriteConnection()) {
            var connection = writeConnection.connection();
            T result;
            try {
                result = supplier.get();
            } catch (Exception exception) {
                connection.rollback();
                throw new TransactionException(exception);
            }
            connection.commit();
            return result;
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
