package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.*;

public class NotePersistence extends Persistence {
    public static void create(final Note... notes) {
        for (final var note : notes) {
            final var sql = new Sql.Builder()
                .insertInto(Note.TABLE)
                .values(
                    note.id,
                    note.userId,
                    note.title,
                    note.content,
                    note.creationTime,
                    note.modificationTime
                );
            executeUpdate(sql.build());
        }
    }

    public static List<Note> read(final UUID userId) {
        final var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where(
                eq(Note.USER_ID, userId)
            );
        return executeQuery(sql.build(), Note::fromResultSet);
    }

    public static PaginatedResult<Note> read(final UUID userId, final Set<UUID> ids, final Pagination pagination) {
        final var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where();
        if (!ids.isEmpty()) {
            sql.in(Note.ID, ids)
                .and();
        }
        sql.eq(Note.USER_ID, userId)
            .limit(limit(pagination.pageSize()))
            .offset(offset(pagination.pageNumber(), pagination.pageSize()));
        final var result = executeQuery(sql.build(), Note::fromResultSet);
        return paginatedResult(pagination, result);
    }

    public static List<Note> readForUpdate(final UUID userId) {
        final var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where(
                eq(Note.USER_ID, userId)
            )
            .forUpdate();
        return executeQuery(sql.build(), Note::fromResultSet);
    }

    public static Note readForUpdate(final UUID userId, final UUID id) {
        final var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where(
                eq(Note.ID, id),
                and(),
                eq(Note.USER_ID, userId)
            )
            .forUpdate();
        final var result = executeQuery(sql.build(), Note::fromResultSet);
        return singleResult(result);
    }

    public static List<Note> readForUpdate(final UUID userId, final Set<UUID> ids) {
        final var sql = new Sql.Builder()
            .select()
            .from(Note.TABLE)
            .where(
                eq(Note.USER_ID, userId),
                and(),
                in(Note.ID, ids)
            )
            .forUpdate();
        return executeQuery(sql.build(), Note::fromResultSet);
    }

    public static boolean update(final Note note) {
        final var sql = new Sql.Builder()
            .update(Note.TABLE)
            .set(
                eq(Note.TITLE, note.title),
                eq(Note.CONTENT, note.content),
                eq(Note.MODIFICATION_TIME, note.modificationTime)
            )
            .where(
                eq(Note.ID, note.id)
            );
        final var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(final Note note) {
        final var sql = new Sql.Builder()
            .delete()
            .from(Note.TABLE)
            .where(
                eq(Note.ID, note.id)
            );
        final var result = executeUpdate(sql.build());
        return boolResult(result);
    }
}
