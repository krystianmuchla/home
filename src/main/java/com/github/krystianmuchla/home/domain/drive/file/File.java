package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;

import java.util.UUID;

public class File extends Model<File.Field> {
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
        updates.put(Field.STATUS, status);
    }

    public void updateName(String name) {
        updates.put(Field.NAME, name);
    }

    public void updateModificationTime() {
        updates.put(Field.MODIFICATION_TIME, new Time());
    }

    public void updateVersion() {
        updates.put(Field.VERSION, version + 1);
    }

    public enum Field {
        ID,
        USER_ID,
        STATUS,
        DIRECTORY_ID,
        NAME,
        CREATION_TIME,
        MODIFICATION_TIME,
        VERSION
    }
}
