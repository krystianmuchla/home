package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RemovedNote extends Entity {
    public static final String TABLE = "removed_note";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String REMOVAL_TIME = "removal_time";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";

    public final UUID id;
    public final UUID userId;
    public final Time removalTime;
    public final Time creationTime;
    public final Time modificationTime;
    public final Integer version;

    public RemovedNote(
        UUID id,
        UUID userId,
        Time removalTime,
        Time creationTime,
        Time modificationTime,
        Integer version
    ) throws RemovedNoteValidationException {
        this.id = id;
        this.userId = userId;
        this.removalTime = removalTime;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
        RemovedNoteValidator.validate(this);
    }

    public RemovedNote(UUID id, UUID userId, Time removalTime) throws RemovedNoteValidationException {
        this(id, userId, removalTime, null, null, null);
    }

    public void updateRemovalTime(Time removalTime) {
        updates.put(REMOVAL_TIME, removalTime);
    }

    public Note asNote() {
        try {
            return new Note(id, userId, removalTime);
        } catch (NoteValidationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static RemovedNote fromResultSet(ResultSet resultSet) {
        try {
            return new RemovedNote(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                TimeFactory.create(resultSet.getTimestamp(REMOVAL_TIME)),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | RemovedNoteValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
