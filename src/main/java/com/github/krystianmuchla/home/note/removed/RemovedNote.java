package com.github.krystianmuchla.home.note.removed;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public record RemovedNote(UUID id, UUID userId, Instant creationTime, Instant modificationTime) {
    public static final String TABLE = "removed_note";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";

    public RemovedNote {
        if (id == null) {
            throw new InternalException("Id cannot be null");
        }
        if (userId == null) {
            throw new InternalException("User id cannot be null");
        }
        if (creationTime == null) {
            throw new InternalException("Creation time cannot be null");
        }
        if (modificationTime == null) {
            throw new InternalException("Modification time cannot be null");
        }
    }

    public RemovedNote(UUID id, UUID userId, Instant creationTime) {
        this(id, userId, creationTime, creationTime);
    }

    public Note asNote() {
        return new Note(id, userId, modificationTime);
    }

    public static RemovedNote fromResultSet(ResultSet resultSet) {
        try {
            return new RemovedNote(
                UUID.fromString(resultSet.getString(ID)),
                UUID.fromString(resultSet.getString(USER_ID)),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME))
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
