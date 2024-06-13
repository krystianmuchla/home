package com.github.krystianmuchla.home.id.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record User(UUID id) {
    public static final String TABLE = "user";
    public static final String ID = "id";

    public User(final ResultSet resultSet) throws SQLException {
        this(UUID.fromString(resultSet.getString(ID)));
    }
}
