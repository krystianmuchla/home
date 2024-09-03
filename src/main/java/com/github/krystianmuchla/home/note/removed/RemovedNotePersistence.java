package com.github.krystianmuchla.home.note.removed;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.*;

public class RemovedNotePersistence extends Persistence {
    public static void create(final RemovedNote... removedNotes) {
        for (final var removedNote : removedNotes) {
            final var sql = new Sql.Builder()
                .insertInto(RemovedNote.TABLE)
                .values(
                    removedNote.id(),
                    removedNote.userId(),
                    removedNote.creationTime(),
                    removedNote.modificationTime()
                );
            executeUpdate(sql.build());
        }
    }

    public static List<RemovedNote> readForUpdate(final UUID userId) {
        final var sql = new Sql.Builder()
            .select()
            .from(RemovedNote.TABLE)
            .where(
                eq(RemovedNote.USER_ID, userId)
            )
            .forUpdate();
        return executeQuery(sql.build(), RemovedNote::fromResultSet);
    }

    public static boolean update(final RemovedNote removedNote) {
        final var sql = new Sql.Builder()
            .update(RemovedNote.TABLE)
            .set(
                eq(RemovedNote.CREATION_TIME, removedNote.creationTime()),
                eq(RemovedNote.MODIFICATION_TIME, removedNote.modificationTime())
            )
            .where(
                eq(RemovedNote.ID, removedNote.id())
            );
        final var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static void delete(final RemovedNote removedNote) {
        final var sql = new Sql.Builder()
            .delete()
            .from(RemovedNote.TABLE)
            .where(
                eq(RemovedNote.ID, removedNote.id())
            );
        executeUpdate(sql.build());
    }

    public static void delete(final Instant creationTime) {
        final var sql = new Sql.Builder()
            .delete()
            .from(RemovedNote.TABLE)
            .where(
                lt(RemovedNote.CREATION_TIME, creationTime)
            );
        executeUpdate(sql.build());
    }
}
