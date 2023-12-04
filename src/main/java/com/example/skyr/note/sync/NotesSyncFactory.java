package com.example.skyr.note.sync;

import com.example.skyr.InstantFactory;
import com.example.skyr.exception.ServiceErrorException;
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
