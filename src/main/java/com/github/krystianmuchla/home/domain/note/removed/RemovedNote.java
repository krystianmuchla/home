package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.infrastructure.persistence.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public class RemovedNote extends Entity {
    public static final String TABLE = "removed_note";
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String REMOVAL_TIME = "removal_time";
    public static final String CREATION_TIME = "creation_time";
    public static final String MODIFICATION_TIME = "modification_time";
    public static final String VERSION = "version";
    public static final int VERSION_MIN_VALUE = 1;

    public final UUID id;
    public final UUID userId;
    public final Instant removalTime;
    public final Instant creationTime;
    public final Instant modificationTime;
    public final Integer version;

    public RemovedNote(
        UUID id,
        UUID userId,
        Instant removalTime,
        Instant creationTime,
        Instant modificationTime,
        Integer version
    ) {
        assert id != null;
        assert userId != null;
        assert removalTime != null;
        assert version == null || version >= VERSION_MIN_VALUE;
        this.id = id;
        this.userId = userId;
        this.removalTime = removalTime;
        this.creationTime = creationTime;
        this.modificationTime = modificationTime;
        this.version = version;
    }

    public RemovedNote(UUID id, UUID userId, Instant removalTime) {
        this(id, userId, removalTime, null, null, null);
    }

    public void updateRemovalTime(Instant removalTime) {
        updates.put(REMOVAL_TIME, removalTime);
    }

    public Note asNote() {
        return new Note(id, userId, removalTime);
    }

    public static RemovedNote fromResultSet(ResultSet resultSet) {
        try {
            return new RemovedNote(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                InstantFactory.create(resultSet.getTimestamp(REMOVAL_TIME)),
                InstantFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                InstantFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }
}
