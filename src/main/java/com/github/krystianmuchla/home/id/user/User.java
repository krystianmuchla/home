package com.github.krystianmuchla.home.id.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record User(UUID id) {
    public User(ResultSet resultSet) throws SQLException {
        this(UUID.fromString(resultSet.getString("id")));
    }
}
