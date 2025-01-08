package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
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
        this(id, userId, removalTime, null, null, null);
    }

    public void updateRemovalTime(Time removalTime) {
        updates.put(Field.REMOVAL_TIME, removalTime);
    }

    public void updateModificationTime() {
        updates.put(Field.MODIFICATION_TIME, new Time());
    }

    public void updateVersion() {
        updates.put(Field.VERSION, version + 1);
    }

    public Note asNote() {
        try {
            return new Note(id, userId, removalTime);
        } catch (NoteValidationException exception) {
            throw new IllegalStateException(exception);
        }
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
