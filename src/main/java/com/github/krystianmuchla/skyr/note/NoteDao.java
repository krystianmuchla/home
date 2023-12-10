package com.github.krystianmuchla.skyr.note;

import com.github.krystianmuchla.skyr.Dao;
import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import com.github.krystianmuchla.skyr.pagination.PaginatedResult;
import com.github.krystianmuchla.skyr.pagination.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoteDao extends Dao {
    private final JdbcTemplate jdbcTemplate;

    public void create(final UUID id,
                       final String title,
                       final String content,
                       final Instant creationTime,
                       final Instant modificationTime) {
        jdbcTemplate.update(
                "INSERT INTO note VALUES (?, ?, ?, ?, ?)",
                id.toString(),
                title,
                content,
                timestamp(creationTime).toString(),
                timestamp(modificationTime).toString()
        );
    }

    public boolean delete(final UUID id) {
        final var result = jdbcTemplate.update("DELETE FROM note WHERE id = ?", id.toString());
        return isUpdated(result);
    }

    public Note read(final UUID id) {
        final var result = jdbcTemplate.query(
                "SELECT * FROM note WHERE id = ?",
                mapper(),
                id.toString()
        );
        return singleResult(result);
    }

    public List<Note> read() {
        return jdbcTemplate.query("SELECT * FROM note", mapper());
    }

    public List<Note> readWithLock() {
        return jdbcTemplate.query("SELECT * FROM note FOR UPDATE", mapper());
    }

    public PaginatedResult<Note> read(final Pagination pagination) {
        final var result = jdbcTemplate.query(
                "SELECT * FROM note LIMIT ? OFFSET ?",
                mapper(),
                limit(pagination.pageSize()),
                offset(pagination.pageNumber(), pagination.pageSize())
        );
        return paginatedResult(pagination, result);
    }

    public boolean update(final UUID id,
                          final String title,
                          final String content,
                          final Instant creationTime,
                          final Instant modificationTime) {
        final var parameters = new LinkedHashMap<String, String>();
        if (title != null) parameters.put(Note.TITLE, title);
        if (content != null) parameters.put(Note.CONTENT, content);
        if (creationTime != null) parameters.put(Note.CREATION_TIME, timestamp(creationTime).toString());
        if (modificationTime != null) parameters.put(Note.MODIFICATION_TIME, timestamp(modificationTime).toString());
        if (parameters.isEmpty()) throw new ServerErrorException("Update parameters cannot be empty");
        final var setters = setters(parameters);
        parameters.put(Note.ID, id.toString());
        final var result = jdbcTemplate.update(
                "UPDATE note SET " + setters + " WHERE id = ?",
                parameters.values().toArray()
        );
        return isUpdated(result);
    }

    private RowMapper<Note> mapper() {
        return (resultSet, rowNum) -> NoteFactory.create(resultSet);
    }
}
