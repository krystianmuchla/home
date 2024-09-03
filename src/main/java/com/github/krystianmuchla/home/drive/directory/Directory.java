package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class Directory {
    public static final String TABLE = "directory";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String STATUS = "status";
    public static final String PARENT_ID = "parent_id";
    public static final String NAME = "name";
    public static final int NAME_MAX_LENGTH = 255;
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";

    public final UUID id;
    public final UUID userId;
    public DirectoryStatus status;
    public final UUID parentId;
    public final String name;
    public final Instant creationTime;
    public final Instant modificationTime;

    public Directory(
        UUID id,
        UUID userId,
        DirectoryStatus status,
        UUID parentId,
        String name,
        Instant creationTime,
        Instant modificationTime
    ) {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        this.id = id;
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        this.userId = userId;
        if (status == null) {
            throw new InternalException("Status cannot be null");
        }
        this.status = status;
        this.parentId = parentId;
        if (name == null) {
            throw new InternalException("Name cannot be null");
        }
        if (name.length() > NAME_MAX_LENGTH) {
            throw new InternalException("Name exceeded max length of " + NAME_MAX_LENGTH);
        }
        this.name = name;
        if (creationTime == null) {
            throw new InternalException("Creation time cannot be null");
        }
        this.creationTime = creationTime;
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
        this.modificationTime = creationTime;
    }

    public Directory(
        UUID id,
        UUID userId,
        DirectoryStatus status,
        UUID parentId,
        String name,
        Instant creationTime
    ) {
        this(id, userId, status, parentId, name, creationTime, creationTime);
    }

    public static Directory fromResultSet(ResultSet resultSet) {
        try {
            var parentId = resultSet.getString(PARENT_ID);
            return new Directory(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                DirectoryStatus.valueOf(resultSet.getString(STATUS)),
                parentId == null ? null : UUID.fromString(parentId),
                resultSet.getString(NAME),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
