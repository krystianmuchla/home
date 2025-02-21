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
        this(UUID.randomUUID(), userId, DirectoryStatus.CREATED, parentId, name, new Time(), new Time(), 1);
    }

    public boolean isRemoved() {
        return status == DirectoryStatus.REMOVED;
    }

    public void updateStatus(DirectoryStatus status) throws DirectoryValidationException {
        new DirectoryValidator(this).checkStatus(status).validate();
        switch (this.status) {
            case CREATED, REMOVED -> {
                assert status == DirectoryStatus.REMOVED;
            }
        }
        updates.put(Field.STATUS, status);
    }

    public void updateParentId(UUID parentId) throws DirectoryValidationException {
        new DirectoryValidator(this).checkParentId(parentId).validate();
        updates.put(Field.PARENT_ID, parentId);
    }

    public void updateName(String name) throws DirectoryValidationException {
        new DirectoryValidator(this).checkName(name).validate();
        updates.put(Field.NAME, name);
    }

    public void updateModificationTime(Time modificationTime) throws DirectoryValidationException {
        new DirectoryValidator(this).checkModificationTime(modificationTime).validate();
        updates.put(Field.MODIFICATION_TIME, modificationTime);
    }

    public void updateVersion(Integer version) throws DirectoryValidationException {
        new DirectoryValidator(this).checkVersion(version).validate();
        updates.put(Field.VERSION, version);
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
