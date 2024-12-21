package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class User {
    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";

    public final UUID id;
    public final String name;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public User(UUID id, String name, Instant creationTime, Instant modificationTime, Integer version) throws UserValidationException {
        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        UserValidator.validate(this);
    }

    public User(String name) throws UserValidationException {
        this(UUID.randomUUID(), name, null, null, null);
    }

    public static User fromResultSet(ResultSet resultSet) {
        try {
            return new User(
                UUIDFactory.create(resultSet.getString(ID)),
                resultSet.getString(NAME),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | UserValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
