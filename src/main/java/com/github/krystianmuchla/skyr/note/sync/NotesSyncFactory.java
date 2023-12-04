package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.exception.ServiceErrorException;
import com.github.krystianmuchla.skyr.InstantFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotesSyncFactory {
    public static NotesSync create(final ResultSet resultSet) {
        try {
            final var syncId = UUID.fromString(resultSet.getString(NotesSync.SYNC_ID));
            final var syncTime = InstantFactory.create(resultSet.getTimestamp(NotesSync.SYNC_TIME));
            return new NotesSync(syncId, syncTime);
        } catch (final SQLException exception) {
            throw new ServiceErrorException(exception);
        }
    }
}
