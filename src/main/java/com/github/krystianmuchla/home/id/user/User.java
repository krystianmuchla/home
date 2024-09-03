package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record User(UUID id, String name, Instant creationTime, Instant modificationTime) {
    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final int NAME_MAX_LENGTH = 100;

    public User {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (name == null) {
            throw new InternalException("Name cannot be null");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new InternalException("Name exceeded max length of " + NAME_MAX_LENGTH);
        }
        if (creationTime == null) {
            throw new InternalException("Creation time cannot be null");
        }
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
    }

    public User(UUID id, String name, Instant creationTime) {
        this(id, name, creationTime, creationTime);
    }

    public static User fromResultSet(ResultSet resultSet) {
        try {
            return new User(
                UUID.fromString(resultSet.getString(ID)),
                resultSet.getString(NAME),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
