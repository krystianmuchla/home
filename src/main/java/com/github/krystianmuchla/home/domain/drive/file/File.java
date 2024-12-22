package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    public final UUID id;
    public final UUID userId;
    public final FileStatus status;
    public final UUID directoryId;
    public final String name;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public File(
        UUID id,
        UUID userId,
        FileStatus status,
        UUID directoryId,
        String name,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws FileValidationException {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.directoryId = directoryId;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        FileValidator.validate(this);
    }

    public File(UUID userId, UUID directoryId, String name) throws FileValidationException {
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
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | FileValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
