package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.eq;
import static com.github.krystianmuchla.home.db.Sql.lt;

public class NoteGravePersistence extends Persistence {
    public static void create(final NoteGrave... noteGraves) {
        for (final var noteGrave : noteGraves) {
            final var sql = new Sql.Builder()
                .insertInto(NoteGrave.TABLE)
                .values(
                    noteGrave.id(),
                    noteGrave.userId(),
                    noteGrave.creationTime()
                );
            executeUpdate(sql.build());
        }
    }

    public static List<NoteGrave> readForUpdate(final UUID userId) {
        final var sql = new Sql.Builder()
            .select()
            .from(NoteGrave.TABLE)
            .where(
                eq(NoteGrave.USER_ID, userId)
            )
            .forUpdate();
        return executeQuery(sql.build(), NoteGrave::fromResultSet);
    }

    public static boolean update(final NoteGrave noteGrave) {
        final var sql = new Sql.Builder()
            .update(NoteGrave.TABLE)
            .set(
                eq(NoteGrave.CREATION_TIME, noteGrave.creationTime())
            )
            .where(
                eq(NoteGrave.ID, noteGrave.id())
            );
        final var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static void delete(final NoteGrave noteGrave) {
        final var sql = new Sql.Builder()
            .delete()
            .from(NoteGrave.TABLE)
            .where(
                eq(NoteGrave.ID, noteGrave.id())
            );
        executeUpdate(sql.build());
    }

    public static void delete(final Instant creationTime) {
        final var sql = new Sql.Builder()
            .delete()
            .from(NoteGrave.TABLE)
            .where(
                lt(NoteGrave.CREATION_TIME, creationTime)
            );
        executeUpdate(sql.build());
    }
}
