package com.github.krystianmuchla.home.mnemo;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteDao extends Dao {
    public static final NoteDao INSTANCE = new NoteDao();

    public void create(
        final UUID id,
        final String title,
        final String content,
        final Instant creationTime,
        final Instant modificationTime
    ) {
        executeUpdate(
            "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
            id.toString(),
            title,
            content,
            timestamp(creationTime).toString(),
            timestamp(modificationTime).toString()
        );
    }

    public List<Note> read() {
        return executeQuery("SELECT * FROM note", Note::new);
    }

    public List<Note> readWithLock() {
        return executeQuery("SELECT * FROM note FOR UPDATE", Note::new);
    }

    public Note read(final UUID id) {
        final var result = executeQuery("SELECT * FROM note WHERE id = ?", Note::new, id.toString());
        return singleResult(result);
    }

    public PaginatedResult<Note> read(final Pagination pagination) {
        final var result = executeQuery(
            "SELECT * FROM note LIMIT ? OFFSET ?",
            Note::new,
            limit(pagination.pageSize()),
            offset(pagination.pageNumber(), pagination.pageSize())
        );
        return paginatedResult(pagination, result);
    }

    public boolean update(final UUID id,
                          final String title,
                          final String content,
                          final Instant modificationTime) {
        final var parameters = new LinkedHashMap<String, String>();
        if (title != null) parameters.put("title", title);
        if (content != null) parameters.put("content", content);
        if (modificationTime != null) {
            parameters.put("modification_time", timestamp(modificationTime).toString());
        }
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("Update parameters cannot be empty");
        }
        final var setters = setters(parameters);
        parameters.put("id", id.toString());
        final var result = executeUpdate(
            "UPDATE note SET " + setters + " WHERE id = ?",
            parameters.values().toArray()
        );
        return isUpdated(result);
    }

    public boolean delete(final UUID id) {
        final var result = executeUpdate("DELETE FROM note WHERE id = ?", id.toString());
        return isUpdated(result);
    }
}
