package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class File {
    public static final String TABLE = "file";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String STATUS = "status";
    public static final String DIRECTORY_ID = "directory_id";
    public static final String NAME = "name";
    public static final int NAME_MAX_LENGTH = 255;
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";

    private final UUID id;
    private final UUID userId;
    private FileStatus status;
    private final UUID directoryId;
    private final String name;
    private final Instant creationTime;
    private Instant modificationTime;

    public File(
        UUID id,
        UUID userId,
        FileStatus status,
        UUID directoryId,
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
        this.directoryId = directoryId;
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
        this.modificationTime = modificationTime;
    }

    public File(
        UUID id,
        UUID userId,
        FileStatus status,
        UUID directoryId,
        String name,
        Instant creationTime
    ) {
        this(id, userId, status, directoryId, name, creationTime, creationTime);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public FileStatus getStatus() {
        return status;
    }

    public boolean isUploaded() {
        return this.status == FileStatus.UPLOADED;
    }

    public void upload() {
        var status = FileStatus.UPLOADED;
        if (this.status != FileStatus.INITIATED) {
            throw new InternalException("Cannot change status to %s from %s".formatted(status, this.status));
        }
        this.status = status;
    }

    public boolean isRemoved() {
        return this.status == FileStatus.REMOVED;
    }

    public void remove() {
        var status = FileStatus.REMOVED;
        if (this.status != FileStatus.UPLOADED) {
            throw new InternalException("Cannot change status to %s from %s".formatted(status, this.status));
        }
        this.status = status;
    }

    public UUID getDirectoryId() {
        return directoryId;
    }

    public String getName() {
        return name;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Instant getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(Instant modificationTime) {
        this.modificationTime = modificationTime;
    }

    public static File fromResultSet(ResultSet resultSet) {
        try {
            var directoryId = resultSet.getString(DIRECTORY_ID);
            return new File(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                FileStatus.valueOf(resultSet.getString(STATUS)),
                directoryId == null ? null : UUID.fromString(resultSet.getString(DIRECTORY_ID)),
                resultSet.getString(NAME),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
