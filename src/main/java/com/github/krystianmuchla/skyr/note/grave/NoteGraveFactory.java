package com.github.krystianmuchla.skyr.note.grave;

import com.github.krystianmuchla.skyr.InstantFactory;
import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteGraveFactory {
    public static NoteGrave create(final ResultSet resultSet) {
        final UUID id;
        final Instant creationTime;
        try {
            id = UUID.fromString(resultSet.getString(NoteGrave.ID));
            creationTime = InstantFactory.create(resultSet.getTimestamp(NoteGrave.CREATION_TIME));
        } catch (final SQLException exception) {
            throw new ServerErrorException(exception);
        }
        return new NoteGrave(id, creationTime);
    }
}
