package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
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
    public final Instant contentsModificationTime;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public Note(
        UUID id,
        UUID userId,
        String title,
        String content,
        Instant contentsModificationTime,
        Instant creationTime,
        Instant modificationTime,
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

    public Note(UUID id, UUID userId, String title, String content, Instant contentsModificationTime) throws NoteValidationException {
        this(id, userId, title, content, contentsModificationTime, null, null, null);
    }

    public Note(UUID id, UUID userId, Instant contentsModificationTime) throws NoteValidationException {
        this(id, userId, null, null, contentsModificationTime);
    }

    public void updateTitle(String title) {
        updates.put(TITLE, title);
    }

    public void updateContent(String content) {
        updates.put(CONTENT, content);
    }

    public void updateContentsModificationTime(Instant contentsModificationTime) {
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
                InstantFactory.create(resultSet.getTimestamp(CONTENTS_MODIFICATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | NoteValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
