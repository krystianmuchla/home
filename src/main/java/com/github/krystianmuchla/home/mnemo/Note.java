package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.mnemo.grave.NoteGrave;
import com.github.krystianmuchla.home.mnemo.sync.NoteRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record Note(UUID id, UUID userId, String title, String content, Instant creationTime, Instant modificationTime) {
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int CONTENT_MAX_LENGTH = 65535;

    public Note {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            throw new InternalException("Note title exceeded max length of " + TITLE_MAX_LENGTH);
        }
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new InternalException("Note content exceeded max length of " + CONTENT_MAX_LENGTH);
        }
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
    }

    public Note(final UUID id, final UUID userId, final Instant modificationTime) {
        this(id, userId, null, null, null, modificationTime);
    }

    public Note(final UUID userId, final NoteRequest request) {
        this(
            request.id(),
            userId,
            request.title(),
            request.content(),
            request.creationTime().toInstant(),
            request.modificationTime().toInstant()
        );
    }

    public Note(final ResultSet resultSet) throws SQLException {
        this(
            UUID.fromString(resultSet.getString("id")),
            UUID.fromString(resultSet.getString("user_id")),
            resultSet.getString("title"),
            resultSet.getString("content"),
            InstantFactory.create(resultSet.getTimestamp("creation_time")),
            InstantFactory.create(resultSet.getTimestamp("modification_time"))
        );
    }

    public Note(
        final UUID id,
        final UUID userId,
        final String title,
        final String content,
        final Instant creationTime
    ) {
        this(id, userId, title, content, creationTime, creationTime);
    }

    public NoteGrave asNoteGrave() {
        return new NoteGrave(id, userId, modificationTime);
    }

    public boolean hasContent() {
        return content != null;
    }
}
