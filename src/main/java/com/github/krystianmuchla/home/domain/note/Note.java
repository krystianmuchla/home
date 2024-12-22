package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Note extends Entity {
    public static final String TABLE = "note";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String CONTENTS_MODIFICATION_TIME = "contents_modification_time";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";

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
        updates.put(TITLE, title);
    }

    public void updateContent(String content) {
        updates.put(CONTENT, content);
    }

    public void updateContentsModificationTime(Time contentsModificationTime) {
        updates.put(CONTENTS_MODIFICATION_TIME, contentsModificationTime);
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

    public static Note fromResultSet(ResultSet resultSet) {
        try {
            return new Note(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                resultSet.getString(TITLE),
                resultSet.getString(CONTENT),
                TimeFactory.create(resultSet.getTimestamp(CONTENTS_MODIFICATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | NoteValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
