package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.note.removed.RemovedNote;
import com.github.krystianmuchla.home.note.sync.NoteRequest;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class Note {
    public static final String TABLE = "note";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int CONTENT_MAX_LENGTH = 65535;

    public final UUID id;
    public final UUID userId;
    public String title;
    public String content;
    public final Instant creationTime;
    public Instant modificationTime;

    public Note(
        UUID id,
        UUID userId,
        String title,
        String content,
        Instant creationTime,
        Instant modificationTime
    ) {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        this.id = id;
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        this.userId = userId;
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            throw new InternalException("Note title exceeded max length of " + TITLE_MAX_LENGTH);
        }
        this.title = title;
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new InternalException("Note content exceeded max length of " + CONTENT_MAX_LENGTH);
        }
        this.content = content;
        this.creationTime = creationTime;
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
        this.modificationTime = modificationTime;
    }

    public Note(UUID id, UUID userId, Instant modificationTime) {
        this(id, userId, null, null, null, modificationTime);
    }

    public Note(UUID userId, NoteRequest request) {
        this(
            request.id(),
            userId,
            request.title(),
            request.content(),
            request.creationTime(),
            request.modificationTime()
        );
    }

    public Note(
        UUID id,
        UUID userId,
        String title,
        String content,
        Instant creationTime
    ) {
        this(id, userId, title, content, creationTime, creationTime);
    }

    public RemovedNote asRemovedNote() {
        return new RemovedNote(id, userId, modificationTime);
    }

    public boolean hasContent() {
        return content != null;
    }

    public static Note fromResultSet(ResultSet resultSet) {
        try {
            return new Note(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                resultSet.getString(TITLE),
                resultSet.getString(CONTENT),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
