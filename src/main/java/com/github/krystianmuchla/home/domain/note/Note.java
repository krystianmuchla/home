package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.Model;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;

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
        this(id, userId, title, content, contentsModificationTime, new Time(), new Time(), 1);
    }

    public void updateTitle(String title) throws NoteValidationException {
        new NoteValidator(this).checkTitle(title).validate();
        updates.put(Field.TITLE, title);
    }

    public void updateContent(String content) throws NoteValidationException {
        new NoteValidator(this).checkContent(content).validate();
        updates.put(Field.CONTENT, content);
    }

    public void updateContentsModificationTime(Time contentsModificationTime) throws NoteValidationException {
        new NoteValidator(this).checkContentsModificationTime(contentsModificationTime).validate();
        updates.put(Field.CONTENTS_MODIFICATION_TIME, contentsModificationTime);
    }

    public void updateModificationTime(Time modificationTime) throws NoteValidationException {
        new NoteValidator(this).checkModificationTime(modificationTime).validate();
        updates.put(Field.MODIFICATION_TIME, modificationTime);
    }

    public void updateVersion(Integer version) throws NoteValidationException {
        new NoteValidator(this).checkVersion(version).validate();
        updates.put(Field.VERSION, version);
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
