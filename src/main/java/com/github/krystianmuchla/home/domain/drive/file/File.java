package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Updatable;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;

import java.util.UUID;

public class File extends Updatable<File.Field> {
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
        this(UUID.randomUUID(), userId, FileStatus.UPLOADING, directoryId, name, new Time(), new Time(), 1);
    }

    public boolean isUploaded() {
        return status == FileStatus.UPLOADED;
    }

    public boolean isRemoved() {
        return status == FileStatus.REMOVED;
    }

    public void updateStatus(FileStatus status) throws FileValidationException {
        new FileValidator(this).checkStatus(status).validate();
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

    public void updateDirectoryId(UUID directoryId) throws FileValidationException {
        new FileValidator(this).checkDirectoryId(directoryId).validate();
        updates.put(Field.DIRECTORY_ID, directoryId);
    }

    public void updateName(String name) throws FileValidationException {
        new FileValidator(this).checkName(name).validate();
        updates.put(Field.NAME, name);
    }

    public void updateModificationTime(Time modificationTime) throws FileValidationException {
        new FileValidator(this).checkModificationTime(modificationTime).validate();
        updates.put(Field.MODIFICATION_TIME, new Time());
    }

    public void updateVersion(Integer version) throws FileValidationException {
        new FileValidator(this).checkVersion(version).validate();
        updates.put(Field.VERSION, version);
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
