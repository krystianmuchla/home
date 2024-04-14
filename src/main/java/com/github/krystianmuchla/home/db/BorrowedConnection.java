package com.github.krystianmuchla.home.db;

import java.sql.Connection;
import java.sql.SQLException;

public record BorrowedConnection(Connection connection) implements AutoCloseable {
    @Override
    public void close() throws SQLException {
        ConnectionManager.returnConnection(connection);
    }
}
