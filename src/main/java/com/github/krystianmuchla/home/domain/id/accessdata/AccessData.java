package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AccessData {
    public static final String TABLE = "access_data";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String LOGIN = "login";
    public static final String SALT = "salt";
    public static final String SECRET = "secret";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";

    public final UUID id;
    public final UUID userId;
    public final String login;
    public final byte[] salt;
    public final byte[] secret;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public AccessData(
        UUID id,
        UUID userId,
        String login,
        byte[] salt,
        byte[] secret,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws AccessDataValidationException {
        this.id = id;
        this.userId = userId;
        this.login = login;
        this.salt = salt;
        this.secret = secret;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        AccessDataValidator.validate(this);
    }

    public AccessData(UUID userId, String login, byte[] salt, byte[] secret) throws AccessDataValidationException {
        this(UUID.randomUUID(), userId, login, salt, secret, null, null, null);
    }

    public static AccessData fromResultSet(ResultSet resultSet) {
        try {
            return new AccessData(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                resultSet.getString(LOGIN),
                resultSet.getBytes(SALT),
                resultSet.getBytes(SECRET),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | AccessDataValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
