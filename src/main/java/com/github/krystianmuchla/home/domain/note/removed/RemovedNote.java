package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;

import java.util.UUID;

public class RemovedNote extends Model<RemovedNote.Field> {
    public final UUID id;
    public final UUID userId;
    public final Time removalTime;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public RemovedNote(
        UUID id,
        UUID userId,
        Time removalTime,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws RemovedNoteValidationException {
        this.id = id;
        this.userId = userId;
        this.removalTime = removalTime;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        RemovedNoteValidator.validate(this);
    }

    public RemovedNote(UUID id, UUID userId, Time removalTime) throws RemovedNoteValidationException {
        this(id, userId, removalTime, new Time(), new Time(), 1);
    }

    public void updateRemovalTime(Time removalTime) throws RemovedNoteValidationException {
        new RemovedNoteValidator(this).checkRemovalTime(removalTime).validate();
        updates.put(Field.REMOVAL_TIME, removalTime);
    }

    public void updateModificationTime(Time modificationTime) throws RemovedNoteValidationException {
        new RemovedNoteValidator(this).checkModificationTime(modificationTime).validate();
        updates.put(Field.MODIFICATION_TIME, modificationTime);
    }

    public void updateVersion(Integer version) throws RemovedNoteValidationException {
        new RemovedNoteValidator(this).checkVersion(version).validate();
        updates.put(Field.VERSION, version);
    }

    public enum Field {
        ID,
        USER_ID,
        REMOVAL_TIME,
        CREATION_TIME,
        MODIFICATION_TIME,
        VERSION
    }
}
