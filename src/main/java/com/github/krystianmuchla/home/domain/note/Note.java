package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;

import java.util.UUID;

public class Note extends Model<Note.Field> {
    public final UUID id;
    public final UUID userId;
    public final String title;
    public final String content;
    public final Time contentsModificationTime;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public Note(
        UUID id,
        UUID userId,
        String title,
        String content,
        Time contentsModificationTime,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws NoteValidationException {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.contentsModificationTime = contentsModificationTime;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        NoteValidator.validate(this);
    }

    public Note(UUID id, UUID userId, String title, String content, Time contentsModificationTime) throws NoteValidationException {
        this(id, userId, title, content, contentsModificationTime, null, null, null);
    }

    public Note(UUID id, UUID userId, Time contentsModificationTime) throws NoteValidationException {
        this(id, userId, null, null, contentsModificationTime);
    }

    public void updateTitle(String title) {
        updates.put(Field.TITLE, title);
    }

    public void updateContent(String content) {
        updates.put(Field.CONTENT, content);
    }

    public void updateContentsModificationTime(Time contentsModificationTime) {
        updates.put(Field.CONTENTS_MODIFICATION_TIME, contentsModificationTime);
    }

    public void updateModificationTime() {
        updates.put(Field.MODIFICATION_TIME, new Time());
    }

    public void updateVersion() {
        updates.put(Field.VERSION, version + 1);
    }

    public RemovedNote asRemovedNote() {
        try {
            return new RemovedNote(id, userId, contentsModificationTime);
        } catch (RemovedNoteValidationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public boolean hasContent() {
        return content != null;
    }

    public enum Field {
        ID,
        USER_ID,
        TITLE,
        CONTENT,
        CONTENTS_MODIFICATION_TIME,
        CREATION_TIME,
        MODIFICATION_TIME,
        VERSION
    }
}
