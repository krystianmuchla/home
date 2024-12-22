package com.github.krystianmuchla.home.infrastructure.persistence.core.changelog;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Change(int id, Time executionTime) {
    public static final String ID = "id";
    public static final String EXECUTION_TIME = "execution_time";

    public static Change fromResultSet(ResultSet resultSet) {
        try {
            return new Change(resultSet.getInt(ID), TimeFactory.create(resultSet.getTimestamp(EXECUTION_TIME)));
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
