package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.mnemo.sync.NoteRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record Note(UUID id, String title, String content, Instant creationTime, Instant modificationTime) {
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int CONTENT_MAX_LENGTH = 65535;

    public Note {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            throw new IllegalArgumentException("Note title exceeded max length of " + TITLE_MAX_LENGTH);
        }
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("Note content exceeded max length of " + CONTENT_MAX_LENGTH);
        }
        if (modificationTime == null) {
            throw new IllegalArgumentException("Modification time cannot be null");
        }
    }

    public Note(final UUID id, final Instant modificationTime) {
        this(id, null, null, null, modificationTime);
    }

    public Note(final NoteRequest request) {
        this(
            request.id(),
            request.title(),
            request.content(),
            request.creationTime().toInstant(),
            request.modificationTime().toInstant()
        );
    }

    public Note(final ResultSet resultSet) {
        this(create(resultSet));
    }

    private Note(final Note note) {
        this(note.id, note.title, note.content, note.creationTime, note.modificationTime);
    }

    public boolean hasContent() {
        return content != null;
    }

    private static Note create(final ResultSet resultSet) {
        final UUID id;
        final String title;
        final String content;
        final Instant creationTime;
        final Instant modificationTime;
        try {
            id = UUID.fromString(resultSet.getString("id"));
            title = resultSet.getString("title");
            content = resultSet.getString("content");
            creationTime = InstantFactory.create(resultSet.getTimestamp("creation_time"));
            modificationTime = InstantFactory.create(resultSet.getTimestamp("modification_time"));
        } catch (final SQLException exception) {
            throw new RuntimeException(exception);
        }
        return new Note(id, title, content, creationTime, modificationTime);
    }
}
