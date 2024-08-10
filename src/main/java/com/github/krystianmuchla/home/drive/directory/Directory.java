package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.exception.InternalException;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record Directory(UUID id, UUID userId, UUID parentId, String path) {
    public static final String TABLE = "directory";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String PARENT_ID = "parent_id";
    public static final String PATH = "path";
    public static final int PATH_MAX_LENGTH = 65535;

    public Directory {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        if (path == null) {
            throw new InternalException("Path cannot be null");
        }
        if (path.length() > PATH_MAX_LENGTH) {
            throw new InternalException("Path exceeded max length of " + PATH_MAX_LENGTH);
        }
    }

    public String getName() {
        return Path.of(path).getFileName().toString();
    }

    public static Directory fromResultSet(ResultSet resultSet) {
        try {
            var parentId = resultSet.getString(PARENT_ID);
            return new Directory(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                parentId == null ? null : UUID.fromString(parentId),
                resultSet.getString(PATH)
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
