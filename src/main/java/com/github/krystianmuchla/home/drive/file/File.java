package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.exception.InternalException;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record File(UUID id, UUID userId, UUID directoryId, String path) {
    public static final String TABLE = "file";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String DIRECTORY_ID = "directory_id";
    public static final String PATH = "path";
    public static final int PATH_MAX_LENGTH = 65535;

    public File {
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

    public static File fromResultSet(ResultSet resultSet) {
        try {
            var directoryId = resultSet.getString(DIRECTORY_ID);
            return new File(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                directoryId == null ? null : UUID.fromString(directoryId),
                resultSet.getString(PATH)
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
