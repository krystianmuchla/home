package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record User(UUID id, String name) {
    public static final String TABLE = "user";
    public static final String ID = "id";
    public static final String NAME = "name";
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
    }

    public static User fromResultSet(ResultSet resultSet) {
        try {
            return new User(UUID.fromString(resultSet.getString(ID)), resultSet.getString(NAME));
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
