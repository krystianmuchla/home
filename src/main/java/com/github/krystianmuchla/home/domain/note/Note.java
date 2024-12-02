package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

import static com.github.krystianmuchla.home.domain.id.IdValidator.validateUserId;
import static com.github.krystianmuchla.home.domain.note.NoteValidator.*;

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
    ) {
        assert validateNoteId(id).isEmpty();
        assert validateUserId(userId).isEmpty();
        assert title == null || validateNoteTitle(title).isEmpty();
        assert content == null || validateNoteContent(content).isEmpty();
        assert validateNoteContentsModificationTime(contentsModificationTime).isEmpty();
        assert creationTime == null || validateCreationTime(creationTime).isEmpty();
        assert modificationTime == null || validateModificationTime(modificationTime).isEmpty();
        assert version == null || validateVersion(version).isEmpty();
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.contentsModificationTime = contentsModificationTime;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
    }

    public Note(UUID id, UUID userId, String title, String content, Instant contentsModificationTime) {
        this(id, userId, title, content, contentsModificationTime, null, null, null);
    }

    public Note(UUID id, UUID userId, Instant contentsModificationTime) {
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
        return new RemovedNote(id, userId, contentsModificationTime);
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
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
