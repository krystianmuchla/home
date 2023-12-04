package com.github.krystianmuchla.skyr.note;

import com.github.krystianmuchla.skyr.exception.ServiceErrorException;
import com.github.krystianmuchla.skyr.InstantFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoteFactory {
    public static Note create(final ResultSet resultSet) {
        try {
            final var id = UUID.fromString(resultSet.getString(Note.ID));
            final var title = resultSet.getString(Note.TITLE);
            final var content = resultSet.getString(Note.CONTENT);
            final var creationTime = InstantFactory.create(resultSet.getTimestamp(Note.CREATION_TIME));
            final var modificationTime = InstantFactory.create(resultSet.getTimestamp(Note.MODIFICATION_TIME));
            return new Note(id, title, content, creationTime, modificationTime);
        } catch (final SQLException exception) {
            throw new ServiceErrorException(exception);
        }
    }
}
