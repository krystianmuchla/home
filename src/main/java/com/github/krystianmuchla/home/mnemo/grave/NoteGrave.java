package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.mnemo.Note;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record NoteGrave(UUID id, Instant creationTime) {
    public NoteGrave {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (creationTime == null) {
            throw new IllegalArgumentException("Creation time cannot be null");
        }
    }

    public NoteGrave(final ResultSet resultSet) {
        this(create(resultSet));
    }

    private NoteGrave(final NoteGrave noteGrave) {
        this(noteGrave.id, noteGrave.creationTime);
    }

    public Note toNote() {
        return new Note(id, creationTime);
    }

    private static NoteGrave create(final ResultSet resultSet) {
        final UUID id;
        final Instant creationTime;
        try {
            id = UUID.fromString(resultSet.getString("id"));
            creationTime = InstantFactory.create(resultSet.getTimestamp("creation_time"));
        } catch (final SQLException exception) {
            throw new RuntimeException(exception);
        }
        return new NoteGrave(id, creationTime);
    }
}
