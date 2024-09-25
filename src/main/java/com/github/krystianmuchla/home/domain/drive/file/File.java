package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.infrastructure.persistence.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class File extends Entity {
    public static final String TABLE = "file";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String STATUS = "status";
    public static final String DIRECTORY_ID = "directory_id";
    public static final String NAME = "name";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";
    public static final int NAME_MAX_LENGTH = 255;
    public static final int VERSION_MIN_VALUE = 1;

    public final UUID id;
    public final UUID userId;
    public final FileStatus status;
    public final UUID directoryId;
    public final String name;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public File(
        UUID id,
        UUID userId,
        FileStatus status,
        UUID directoryId,
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
        this.directoryId = directoryId;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
    }

    public File(UUID userId, UUID directoryId, String name) {
        this(UUID.randomUUID(), userId, FileStatus.UPLOADING, directoryId, name, null, null, null);
    }

    public boolean isUploaded() {
        return status == FileStatus.UPLOADED;
    }

    public boolean isRemoved() {
        return status == FileStatus.REMOVED;
    }

    public void updateStatus(FileStatus status) {
        switch (this.status) {
            case UPLOADING -> {
                assert status == FileStatus.UPLOADED;
            }
            case UPLOADED, REMOVED -> {
                assert status == FileStatus.REMOVED;
            }
        }
        updates.put(STATUS, status);
    }

    public static File fromResultSet(ResultSet resultSet) {
        try {
            return new File(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                FileStatus.valueOf(resultSet.getString(STATUS)),
                UUIDFactory.create(resultSet.getString(DIRECTORY_ID)),
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
