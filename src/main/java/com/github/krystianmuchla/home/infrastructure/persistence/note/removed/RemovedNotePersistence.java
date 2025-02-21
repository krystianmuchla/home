package com.github.krystianmuchla.home.infrastructure.persistence.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNote;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.*;
import static com.github.krystianmuchla.home.infrastructure.persistence.note.removed.RemovedNoteColumn.*;

public class RemovedNotePersistence extends Persistence {
    private static final String TABLE = "removed_note";
    private static final Function<RemovedNote.Field, String> COLUMNS;

    static {
        COLUMNS = field -> switch (field) {
            case ID -> ID;
            case USER_ID -> USER_ID;
            case REMOVAL_TIME -> REMOVAL_TIME;
            case CREATION_TIME -> CREATION_TIME;
            case MODIFICATION_TIME -> MODIFICATION_TIME;
            case VERSION -> VERSION;
        };
    }

    public static void create(RemovedNote... removedNotes) {
        for (var removedNote : removedNotes) {
            var sql = new Builder()
                .insertInto(TABLE)
                .values(
                    removedNote.id,
                    removedNote.userId,
                    removedNote.removalTime,
                    removedNote.creationTime,
                    removedNote.modificationTime,
                    removedNote.version
                );
            executeUpdate(sql.build());
        }
    }

    public static List<RemovedNote> read(UUID userId) {
        var sql = new Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId)
            );
        return executeQuery(sql.build(), RemovedNotePersistence::map);
    }

    public static boolean update(RemovedNote removedNote) {
        var updates = removedNote.consumeUpdates();
        var sql = new Builder()
            .update(TABLE)
            .set(toSql(updates, COLUMNS))
            .where(
                eq(ID, removedNote.id),
                and(),
                eq(USER_ID, removedNote.userId),
                and(),
                eq(VERSION, removedNote.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(RemovedNote removedNote) {
        var sql = new Builder()
            .delete()
            .from(TABLE)
            .where(
                eq(ID, removedNote.id),
                and(),
                eq(USER_ID, removedNote.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static void delete(Time creationTime) {
        var sql = new Builder()
            .delete()
            .from(TABLE)
            .where(
                lt(CREATION_TIME, creationTime)
            );
        executeUpdate(sql.build());
    }

    public static RemovedNote map(ResultSet resultSet) {
        try {
            return new RemovedNote(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                TimeFactory.create(resultSet.getTimestamp(REMOVAL_TIME)),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | RemovedNoteValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
