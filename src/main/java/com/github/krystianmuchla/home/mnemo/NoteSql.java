package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NoteSql extends Sql {
    public static void create(final Note... notes) {
        for (final var note : notes) {
            executeUpdate(
                "INSERT INTO %s VALUES (?, ?, ?, ?, ?, ?)".formatted(Note.NOTE),
                note.id().toString(),
                note.userId().toString(),
                note.title(),
                note.content(),
                timestamp(note.creationTime()).toString(),
                timestamp(note.modificationTime()).toString());
        }
    }

    public static List<Note> readByUserId(final UUID userId) {
        return executeQuery(
            "SELECT * FROM %s WHERE %s = ?".formatted(Note.NOTE, Note.USER_ID),
            mapper(),
            userId.toString()
        );
    }

    public static PaginatedResult<Note> readByUserId(final UUID userId, final Pagination pagination) {
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ? LIMIT ? OFFSET ?".formatted(Note.NOTE, Note.USER_ID),
            mapper(),
            userId.toString(),
            limit(pagination.pageSize()),
            offset(pagination.pageNumber(), pagination.pageSize())
        );
        return paginatedResult(pagination, result);
    }

    public static List<Note> readByUserIdWithLock(final UUID userId) {
        return executeQuery(
            "SELECT * FROM %s WHERE %s = ? FOR UPDATE".formatted(Note.NOTE, Note.USER_ID),
            mapper(),
            userId.toString()
        );
    }

    public static Note readByIdAndUserId(final UUID id, final UUID userId) {
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ? AND %s = ?".formatted(Note.NOTE, Note.ID, Note.USER_ID),
            mapper(),
            id.toString(),
            userId.toString());
        return singleResult(result);
    }

    public static boolean update(
        final UUID id,
        final UUID userId,
        final String title,
        final String content,
        final Instant modificationTime
    ) {
        final var parameters = new LinkedHashMap<String, String>();
        if (title != null) {
            parameters.put(Note.TITLE, title);
        }
        if (content != null) {
            parameters.put(Note.CONTENT, content);
        }
        if (modificationTime != null) {
            parameters.put(Note.MODIFICATION_TIME, timestamp(modificationTime).toString());
        }
        if (parameters.isEmpty()) {
            throw new InternalException("Update parameters cannot be empty");
        }
        final var setters = setters(parameters);
        parameters.put("id", id.toString());
        parameters.put("user_id", userId.toString());
        final var result = executeUpdate(
            "UPDATE %s SET %s WHERE %s = ? AND %s = ?".formatted(Note.NOTE, setters, Note.ID, Note.USER_ID),
            parameters.values().toArray());
        return boolResult(result);
    }

    public static boolean delete(final Note note) {
        return deleteByIdAndUserId(note.id(), note.userId());
    }

    public static boolean deleteByIdAndUserId(final UUID id, final UUID userId) {
        final var result = executeUpdate(
            "DELETE FROM %s WHERE %s = ? AND %s = ?".formatted(Note.NOTE, Note.ID, Note.USER_ID),
            id.toString(),
            userId.toString());
        return boolResult(result);
    }

    public static Function<ResultSet, Note> mapper() {
        return resultSet -> {
            try {
                return new Note(resultSet);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }
}
