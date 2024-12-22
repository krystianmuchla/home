package com.github.krystianmuchla.home.infrastructure.persistence.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.and;
import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;

public class NotePersistence extends Persistence {
    public static void create(Note... notes) {
        for (var note : notes) {
            var creationTime = new Time();
            var sql = new Sql.Builder()
                .insertInto(Note.TABLE)
                .values(
                    note.id,
                    note.userId,
                    note.title,
                    note.content,
                    note.contentsModificationTime,
                    creationTime,
                    creationTime,
                    1
                );
            executeUpdate(sql.build());
        }
    }

    public static List<Note> read(UUID userId) {
        var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where(
                eq(Note.USER_ID, userId)
            );
        return executeQuery(sql.build(), Note::fromResultSet);
    }

    public static boolean update(Note note) {
        var updates = note.consumeUpdates();
        updates.put(Note.MODIFICATION_TIME, new Time());
        updates.put(Note.VERSION, note.version + 1);
        var sql = new Sql.Builder()
            .update(Note.TABLE)
            .set(toSql(updates))
            .where(
                eq(Note.ID, note.id),
                and(),
                eq(Note.USER_ID, note.userId),
                and(),
                eq(Note.VERSION, note.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(Note note) {
        var sql = new Sql.Builder()
            .delete()
            .from(Note.TABLE)
            .where(
                eq(Note.ID, note.id),
                and(),
                eq(Note.USER_ID, note.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }
}
