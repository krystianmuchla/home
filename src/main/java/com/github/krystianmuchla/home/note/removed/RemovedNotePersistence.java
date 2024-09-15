package com.github.krystianmuchla.home.note.removed;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.util.InstantFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.*;

public class RemovedNotePersistence extends Persistence {
    public static void create(RemovedNote... removedNotes) {
        for (var removedNote : removedNotes) {
            var creationTime = InstantFactory.create();
            var sql = new Sql.Builder()
                .insertInto(RemovedNote.TABLE)
                .values(
                    removedNote.id,
                    removedNote.userId,
                    removedNote.removalTime,
                    creationTime,
                    creationTime,
                    1
                );
            executeUpdate(sql.build());
        }
    }

    public static List<RemovedNote> read(UUID userId) {
        var sql = new Sql.Builder()
            .select()
            .from(RemovedNote.TABLE)
            .where(
                eq(RemovedNote.USER_ID, userId)
            );
        return executeQuery(sql.build(), RemovedNote::fromResultSet);
    }

    public static boolean update(RemovedNote removedNote) {
        var updates = removedNote.consumeUpdates();
        updates.put(RemovedNote.MODIFICATION_TIME, InstantFactory.create());
        updates.put(RemovedNote.VERSION, removedNote.version + 1);
        var sql = new Sql.Builder()
            .update(RemovedNote.TABLE)
            .set(toSql(updates))
            .where(
                eq(RemovedNote.ID, removedNote.id),
                and(),
                eq(RemovedNote.USER_ID, removedNote.userId),
                and(),
                eq(RemovedNote.VERSION, removedNote.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(RemovedNote removedNote) {
        var sql = new Sql.Builder()
            .delete()
            .from(RemovedNote.TABLE)
            .where(
                eq(RemovedNote.ID, removedNote.id),
                and(),
                eq(RemovedNote.USER_ID, removedNote.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static void delete(Instant creationTime) {
        var sql = new Sql.Builder()
            .delete()
            .from(RemovedNote.TABLE)
            .where(
                lt(RemovedNote.CREATION_TIME, creationTime)
            );
        executeUpdate(sql.build());
    }
}
