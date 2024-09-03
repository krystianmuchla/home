package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record AccessData(
    UUID id,
    UUID userId,
    String login,
    byte[] salt,
    byte[] secret,
    Instant creationTime,
    Instant modificationTime
) {
    public static final String TABLE = "access_data";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String LOGIN = "login";
    public static final int LOGIN_MAX_LENGTH = 50;
    public static final String SALT = "salt";
    public static final String SECRET = "secret";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";

    public AccessData {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        if (login == null) {
            throw new InternalException("Login cannot be null");
        }
        if (login.length() > LOGIN_MAX_LENGTH) {
            throw new InternalException("Login exceeded max length of " + LOGIN_MAX_LENGTH);
        }
        if (salt == null) {
            throw new InternalException("Salt cannot be null");
        }
        if (secret == null) {
            throw new InternalException("Secret cannot be null");
        }
        if (creationTime == null) {
            throw new InternalException("Creation time cannot be null");
        }
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
    }

    public AccessData(UUID id, UUID userId, String login, byte[] salt, byte[] secret, Instant creationTime) {
        this(id, userId, login, salt, secret, creationTime, creationTime);
    }

    public static AccessData fromResultSet(ResultSet resultSet) {
        try {
            return new AccessData(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                resultSet.getString(LOGIN),
                resultSet.getBytes(SALT),
                resultSet.getBytes(SECRET),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
