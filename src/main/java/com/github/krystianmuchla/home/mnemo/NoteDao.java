package com.github.krystianmuchla.home.mnemo;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;

import lombok.SneakyThrows;

public class NoteDao extends Dao {
    public static final NoteDao INSTANCE = new NoteDao();

    public void create(final Note... notes) {
        for (final var note : notes) {
            executeUpdate(
                    "INSERT INTO note VALUES (?, ?, ?, ?, ?, ?)",
                    note.id().toString(),
                    note.userId().toString(),
                    note.title(),
                    note.content(),
                    timestamp(note.creationTime()).toString(),
                    timestamp(note.modificationTime()).toString());
        }
    }

    public List<Note> read() {
        return executeQuery("SELECT * FROM note", mapper());
    }

    public List<Note> read(final UUID userId) {
        return executeQuery("SELECT * FROM note WHERE user_id = ?", mapper(), userId.toString());
    }

    public List<Note> readWithLock(final UUID userId) {
        return executeQuery("SELECT * FROM note WHERE user_id = ? FOR UPDATE", mapper(), userId.toString());
    }

    public Note read(final UUID id, final UUID userId) {
        final var result = executeQuery("SELECT * FROM note WHERE id = ? AND user_id = ?",
                mapper(),
                id.toString(),
                userId.toString());
        return singleResult(result);
    }

    public PaginatedResult<Note> read(final UUID userId, final Pagination pagination) {
        final var result = executeQuery(
                "SELECT * FROM note WHERE user_id = ? LIMIT ? OFFSET ?",
                mapper(),
                userId.toString(),
                limit(pagination.pageSize()),
                offset(pagination.pageNumber(), pagination.pageSize()));
        return paginatedResult(pagination, result);
    }

    public boolean update(final UUID id,
            final UUID userId,
            final String title,
            final String content,
            final Instant modificationTime) {
        final var parameters = new LinkedHashMap<String, String>();
        if (title != null) {
            parameters.put("title", title);
        }
        if (content != null) {
            parameters.put("content", content);
        }
        if (modificationTime != null) {
            parameters.put("modification_time", timestamp(modificationTime).toString());
        }
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("Update parameters cannot be empty");
        }
        final var setters = setters(parameters);
        parameters.put("id", id.toString());
        parameters.put("user_id", userId.toString());
        final var result = executeUpdate(
                "UPDATE note SET " + setters + " WHERE id = ? AND user_id = ?",
                parameters.values().toArray());
        return isUpdated(result);
    }

    public void delete() {
        executeUpdate("DELETE FROM note");
    }

    public boolean delete(final Note note) {
        return delete(note.id(), note.userId());
    }

    public boolean delete(final UUID id, final UUID userId) {
        final var result = executeUpdate(
                "DELETE FROM note WHERE id = ? AND user_id = ?",
                id.toString(),
                userId.toString());
        return isUpdated(result);
    }

    private Function<ResultSet, Note> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public Note apply(final ResultSet resultSet) {
                return new Note(resultSet);
            }
        };
    }
}
