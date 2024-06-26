package com.github.krystianmuchla.home.id.accessdata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record AccessData(UUID id, UUID userId, String login, byte[] salt, byte[] secret) {
    public static final String TABLE = "access_data";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String LOGIN = "login";
    public static final String SALT = "salt";
    public static final String SECRET = "secret";

    public AccessData(final ResultSet resultSet) throws SQLException {
        this(
            UUID.fromString(resultSet.getString(ID)),
            UUID.fromString(resultSet.getString(USER_ID)),
            resultSet.getString(LOGIN),
            resultSet.getBytes(SALT),
            resultSet.getBytes(SECRET)
        );
    }
}
