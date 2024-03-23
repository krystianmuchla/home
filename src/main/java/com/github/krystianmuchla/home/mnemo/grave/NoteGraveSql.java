package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.error.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NoteGraveSql extends Sql {
    public static void create(final NoteGrave... noteGraves) {
        for (final var noteGrave : noteGraves) {
            executeUpdate(
                "INSERT INTO %s VALUES (?, ?, ?)".formatted(NoteGrave.NOTE_GRAVE),
                noteGrave.id().toString(),
                noteGrave.userId().toString(),
                timestamp(noteGrave.creationTime()).toString()
            );
        }
    }

    public static List<NoteGrave> readByUserIdWithLock(final UUID userId) {
        return executeQuery(
            "SELECT * FROM %s WHERE %s = ? FOR UPDATE".formatted(NoteGrave.NOTE_GRAVE, NoteGrave.USER_ID),
            mapper(),
            userId.toString()
        );
    }

    public static boolean update(final NoteGrave noteGrave) {
        final var result = executeUpdate(
            "UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?".formatted(
                NoteGrave.NOTE_GRAVE,
                NoteGrave.CREATION_TIME,
                NoteGrave.ID,
                NoteGrave.USER_ID
            ),
            timestamp(noteGrave.creationTime()).toString(),
            noteGrave.id().toString(),
            noteGrave.userId().toString()
        );
        return boolResult(result);
    }

    public static void delete(final NoteGrave noteGrave) {
        executeUpdate(
            "DELETE FROM %s WHERE %s = ? AND %s = ?".formatted(NoteGrave.NOTE_GRAVE, NoteGrave.ID, NoteGrave.USER_ID),
            noteGrave.id().toString(),
            noteGrave.userId().toString()
        );
    }

    public static void delete(final Instant creationTimeThreshold) {
        executeUpdate(
            "DELETE FROM %s WHERE %s < ?".formatted(NoteGrave.NOTE_GRAVE, NoteGrave.CREATION_TIME),
            timestamp(creationTimeThreshold).toString());
    }

    public static Function<ResultSet, NoteGrave> mapper() {
        return resultSet -> {
            try {
                return new NoteGrave(resultSet);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }
}
