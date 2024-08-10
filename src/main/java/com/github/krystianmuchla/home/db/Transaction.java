package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.exception.TransactionException;

import java.sql.SQLException;
import java.util.function.Supplier;

public class Transaction {
    public static void run(Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T> T run(Supplier<T> supplier) {
        try (var registeredConnection = ConnectionManager.registerConnection()) {
            var connection = registeredConnection.connection();
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
            throw new InternalException(exception);
        }
    }
}
