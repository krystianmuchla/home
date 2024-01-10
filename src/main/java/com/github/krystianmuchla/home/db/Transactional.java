package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transactional {
    default void transactional(final Connection dbConnection, final Runnable runnable) throws SQLException {
        try {
            runnable.run();
            dbConnection.commit();
        } catch (final Exception exception) {
            dbConnection.rollback();
            throw new RuntimeException(exception);
        }
    }
}
