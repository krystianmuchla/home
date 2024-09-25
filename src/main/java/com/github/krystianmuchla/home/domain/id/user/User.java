package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;

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
    public static final int NAME_MAX_LENGTH = 100;
    public static final int VERSION_MIN_VALUE = 1;

    public final UUID id;
    public final String name;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public User(UUID id, String name, Instant creationTime, Instant modificationTime, Integer version) {
        assert id != null;
        assert name != null && name.length() <= NAME_MAX_LENGTH;
        assert version == null || version >= VERSION_MIN_VALUE;
        this.id = id;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
    }

    public User(String name) {
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
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
