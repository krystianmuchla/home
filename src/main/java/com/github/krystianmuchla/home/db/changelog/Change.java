package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public record Change(int id, Instant executionTime) {
    public static final String ID = "id";
    public static final String EXECUTION_TIME = "execution_time";

    public Change(final ResultSet resultSet) throws SQLException {
        this(resultSet.getInt(ID), InstantFactory.create(resultSet.getTimestamp(EXECUTION_TIME)));
    }
}
