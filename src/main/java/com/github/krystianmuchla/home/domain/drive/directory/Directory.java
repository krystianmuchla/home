package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;

import java.util.UUID;

public class Directory extends Model<Directory.Field> {
    public final UUID id;
    public final UUID userId;
    public final DirectoryStatus status;
    public final UUID parentId;
    public final String name;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public Directory(
        UUID id,
        UUID userId,
        DirectoryStatus status,
        UUID parentId,
        String name,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws DirectoryValidationException {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.parentId = parentId;
        this.name = name;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        DirectoryValidator.validate(this);
    }

    public Directory(UUID userId, UUID parentId, String name) throws DirectoryValidationException {
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
        updates.put(Field.STATUS, status);
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
        PARENT_ID,
        NAME,
        CREATION_TIME,
        MODIFICATION_TIME,
        VERSION
    }
}
