package com.github.krystianmuchla.skyr.note;

import com.github.krystianmuchla.skyr.InstantFactory;
import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteFactory {
    public static Note create(final ResultSet resultSet) {
        final UUID id;
        final String title;
        final String content;
        final Instant creationTime;
        final Instant modificationTime;
        try {
            id = UUID.fromString(resultSet.getString(Note.ID));
            title = resultSet.getString(Note.TITLE);
            content = resultSet.getString(Note.CONTENT);
            creationTime = InstantFactory.create(resultSet.getTimestamp(Note.CREATION_TIME));
            modificationTime = InstantFactory.create(resultSet.getTimestamp(Note.MODIFICATION_TIME));
        } catch (final SQLException exception) {
            throw new ServerErrorException(exception);
        }
        return new Note(id, title, content, creationTime, modificationTime);
    }
}
