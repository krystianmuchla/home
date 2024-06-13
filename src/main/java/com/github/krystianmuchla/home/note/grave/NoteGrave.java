package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record NoteGrave(UUID id, UUID userId, Instant creationTime) {
    public static final String TABLE = "note_grave";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String CREATION_TIME = "creation_time";

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

    public NoteGrave(final ResultSet resultSet) throws SQLException {
        this(
            UUID.fromString(resultSet.getString(ID)),
            UUID.fromString(resultSet.getString(USER_ID)),
            InstantFactory.create(resultSet.getTimestamp(CREATION_TIME))
        );
    }

    public Note asNote() {
        return new Note(id, userId, creationTime);
    }
}
