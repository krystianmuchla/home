package com.github.krystianmuchla.home.id.accessdata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record AccessData(UUID id, UUID userId, String login, byte[] salt, byte[] secret) {
    public AccessData(ResultSet resultSet) throws SQLException {
        this(
            UUID.fromString(resultSet.getString("id")),
            UUID.fromString(resultSet.getString("user_id")),
            resultSet.getString("login"),
            resultSet.getBytes("salt"),
            resultSet.getBytes("secret")
        );
    }
}
