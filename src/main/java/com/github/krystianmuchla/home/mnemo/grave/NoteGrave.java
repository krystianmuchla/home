package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.mnemo.Note;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record NoteGrave(UUID id, UUID userId, Instant creationTime) {
    public NoteGrave {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        if (creationTime == null) {
            throw new InternalException("Creation time cannot be null");
        }
    }

    public NoteGrave(final UUID id, final UUID userId) {
        this(id, userId, InstantFactory.create());
    }

    public NoteGrave(final ResultSet resultSet) throws SQLException {
        this(
            UUID.fromString(resultSet.getString("id")),
            UUID.fromString(resultSet.getString("user_id")),
            InstantFactory.create(resultSet.getTimestamp("creation_time"))
        );
    }

    public Note asNote() {
        return new Note(id, userId, creationTime);
    }
}
