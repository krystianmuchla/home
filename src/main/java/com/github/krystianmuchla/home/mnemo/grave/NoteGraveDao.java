package com.github.krystianmuchla.home.mnemo.grave;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.github.krystianmuchla.home.Dao;

import lombok.SneakyThrows;

public class NoteGraveDao extends Dao {
    public static final NoteGraveDao INSTANCE = new NoteGraveDao();

    public void create(final NoteGrave... noteGraves) {
        for (final var noteGrave: noteGraves) {
            executeUpdate("INSERT INTO note_grave VALUES (?, ?, ?)",
                noteGrave.id().toString(),
                noteGrave.userId().toString(),
                timestamp(noteGrave.creationTime()).toString());
        }
    }

    public List<NoteGrave> read() {
        return executeQuery("SELECT * FROM note_grave", mapper());
    }

    public List<NoteGrave> readWithLock(final UUID userId) {
        return executeQuery("SELECT * FROM note_grave WHERE user_id = ? FOR UPDATE", mapper(), userId.toString());
    }

    public boolean update(final NoteGrave noteGrave) {
        final var result = executeUpdate(
            "UPDATE note_grave SET creation_time = ? WHERE id = ? AND user_id = ?",
            timestamp(noteGrave.creationTime()).toString(),
            noteGrave.id().toString(),
            noteGrave.userId().toString()
        );
        return boolResult(result);
    }

    public void delete() {
        executeUpdate("DELETE FROM note_grave");
    }

    public void delete(final NoteGrave noteGrave) {
        executeUpdate(
            "DELETE FROM note_grave WHERE id = ? AND user_id = ?",
            noteGrave.id().toString(),
            noteGrave.userId().toString());
    }

    public void delete(final Instant creationTimeThreshold) {
        executeUpdate(
                "DELETE FROM note_grave WHERE creation_time < ?",
                timestamp(creationTimeThreshold).toString());
    }

    private Function<ResultSet, NoteGrave> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public NoteGrave apply(final ResultSet resultSet) {
                return new NoteGrave(resultSet);
            }
        };
    }
}
