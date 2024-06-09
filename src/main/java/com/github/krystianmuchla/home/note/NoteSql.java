package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
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

    public static List<Note> read(final UUID userId) {
        return executeQuery(
            "SELECT * FROM %s WHERE %s = ?".formatted(Note.NOTE, Note.USER_ID),
            mapper(),
            userId.toString()
        );
    }

    // todo sql builder
    public static PaginatedResult<Note> read(final UUID userId, final Set<UUID> ids, final Pagination pagination) {
        final List<Note> result;
        if (ids.isEmpty()) {
            result = executeQuery(
                "SELECT * FROM %s WHERE %s = ? LIMIT ? OFFSET ?".formatted(Note.NOTE, Note.USER_ID),
                mapper(),
                userId.toString(),
                limit(pagination.pageSize()),
                offset(pagination.pageNumber(), pagination.pageSize())
            );
        } else {
            result = executeQuery(
                "SELECT * FROM %s WHERE %s IN (?) AND %s = ? LIMIT ? OFFSET ?".formatted(Note.NOTE, Note.ID, Note.USER_ID),
                mapper(),
                join(ids, ","),
                userId.toString(),
                limit(pagination.pageSize()),
                offset(pagination.pageNumber(), pagination.pageSize())
            );
        }
        return paginatedResult(pagination, result);
    }

    public static List<Note> readForUpdate(final UUID userId) {
        return executeQuery(
            "SELECT * FROM %s WHERE %s = ? FOR UPDATE".formatted(Note.NOTE, Note.USER_ID),
            mapper(),
            userId.toString()
        );
    }

    public static Note read(final UUID userId, final UUID id) {
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ? AND %s = ?".formatted(Note.NOTE, Note.ID, Note.USER_ID),
            mapper(),
            id.toString(),
            userId.toString());
        return singleResult(result);
    }

    public static boolean update(
        final UUID userId,
        final UUID id,
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

    public static boolean delete(final UUID userId, final Note note) {
        return delete(userId, List.of(note.id()));
    }

    public static boolean delete(final UUID userId, final Collection<UUID> ids) {
        final var result = executeUpdate(
            "DELETE FROM %s WHERE %s IN (?) AND %s = ?".formatted(Note.NOTE, Note.ID, Note.USER_ID),
            join(ids, ","),
            userId.toString()
        );
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
