package com.github.krystianmuchla.home.infrastructure.persistence.core;

import java.sql.Connection;
import java.sql.SQLException;

public class ReadTransaction implements AutoCloseable {
    public final Connection connection;

    public ReadTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() throws SQLException {
        TransactionManager.finishReadTransaction(this);
    }
}
