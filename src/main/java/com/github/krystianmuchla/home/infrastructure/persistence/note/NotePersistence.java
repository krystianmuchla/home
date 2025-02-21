package com.github.krystianmuchla.home.infrastructure.persistence.note;

import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.and;
import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;
import static com.github.krystianmuchla.home.infrastructure.persistence.note.NoteColumn.*;

public class NotePersistence extends Persistence {
    private static final String TABLE = "note";
    private static final Function<Note.Field, String> COLUMNS;

    static {
        COLUMNS = field -> switch (field) {
            case ID -> ID;
            case USER_ID -> USER_ID;
            case TITLE -> TITLE;
            case CONTENT -> CONTENT;
            case CONTENTS_MODIFICATION_TIME -> CONTENTS_MODIFICATION_TIME;
            case CREATION_TIME -> CREATION_TIME;
            case MODIFICATION_TIME -> MODIFICATION_TIME;
            case VERSION -> VERSION;
        };
    }

    public static void create(Note... notes) {
        for (var note : notes) {
            var sql = new Sql.Builder()
                .insertInto(TABLE)
                .values(
                    note.id,
                    note.userId,
                    note.title,
                    note.content,
                    note.contentsModificationTime,
                    note.creationTime,
                    note.modificationTime,
                    note.version
                );
            executeUpdate(sql.build());
        }
    }

    public static List<Note> read(UUID userId) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId)
            );
        return executeQuery(sql.build(), NotePersistence::map);
    }

    public static boolean update(Note note) {
        var updates = note.consumeUpdates();
        var sql = new Sql.Builder()
            .update(TABLE)
            .set(toSql(updates, COLUMNS))
            .where(
                eq(ID, note.id),
                and(),
                eq(USER_ID, note.userId),
                and(),
                eq(VERSION, note.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(Note note) {
        var sql = new Sql.Builder()
            .delete()
            .from(TABLE)
            .where(
                eq(ID, note.id),
                and(),
                eq(USER_ID, note.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static Note map(ResultSet resultSet) {
        try {
            return new Note(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                resultSet.getString(TITLE),
                resultSet.getString(CONTENT),
                TimeFactory.create(resultSet.getTimestamp(CONTENTS_MODIFICATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | NoteValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
