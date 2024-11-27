package com.github.krystianmuchla.home.infrastructure.persistence.core;

import java.sql.Connection;
import java.sql.SQLException;

public record ReadConnection(Connection connection) implements AutoCloseable {
    @Override
    public void close() throws SQLException {
        ConnectionManager.returnReadConnection(connection);
    }
}
