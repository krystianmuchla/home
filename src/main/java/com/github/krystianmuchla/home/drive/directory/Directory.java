package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.db.Entity;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;
import com.github.krystianmuchla.home.util.UUIDFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class Directory extends Entity {
    public static final String TABLE = "directory";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String STATUS = "status";
    public static final String PARENT_ID = "parent_id";
    public static final String NAME = "name";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";
    public static final int NAME_MAX_LENGTH = 255;
    public static final int VERSION_MIN_VALUE = 1;

    public final UUID id;
    public final UUID userId;
    public final DirectoryStatus status;
    public final UUID parentId;
    public final String name;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public Directory(
        UUID id,
        UUID userId,
        DirectoryStatus status,
        UUID parentId,
        String name,
        Instant creationTime,
        Instant modificationTime,
        Integer version
    ) {
        assert id != null;
        assert userId != null;
        assert status != null;
        assert name != null && name.length() <= NAME_MAX_LENGTH;
        assert version == null || version >= VERSION_MIN_VALUE;
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.parentId = parentId;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
    }

    public Directory(UUID userId, UUID parentId, String name) {
        this(UUID.randomUUID(), userId, DirectoryStatus.CREATED, parentId, name, null, null, null);
    }

    public boolean isRemoved() {
        return status == DirectoryStatus.REMOVED;
    }

    public void updateStatus(DirectoryStatus status) {
        switch (this.status) {
            case CREATED, REMOVED -> {
                assert status == DirectoryStatus.REMOVED;
            }
        }
        updates.put(Directory.STATUS, status);
    }

    public static Directory fromResultSet(ResultSet resultSet) {
        try {
            return new Directory(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                DirectoryStatus.valueOf(resultSet.getString(STATUS)),
                UUIDFactory.create(resultSet.getString(PARENT_ID)),
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
