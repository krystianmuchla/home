package com.example.skyr.note;

import com.example.skyr.Dao;
import com.example.skyr.InstantFactory;
import com.example.skyr.exception.ServiceErrorException;
import com.example.skyr.pagination.PaginatedResult;
import com.example.skyr.pagination.Pagination;
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
public final class NoteDao extends Dao {
    private final JdbcTemplate jdbcTemplate;

    public void create(final Note note) {
        create(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    public void create(final UUID id, final String title, final String content) {
        final var creationTime = InstantFactory.create();
        create(id, title, content, creationTime, creationTime);
    }

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

    public List<Note> readWithLock(final Instant modificationTimeCursor) {
        return jdbcTemplate.query(
                "SELECT * FROM note WHERE modification_time > ? ORDER BY modification_time FOR UPDATE",
                mapper(),
                timestamp(modificationTimeCursor)
        );
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

    public void update(final Note note) {
        update(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }

    public void update(final UUID id,
                       final String title,
                       final String content,
                       final Instant creationTime,
                       final Instant modificationTime) {
        final var parameters = new LinkedHashMap<String, String>();
        if (title != null) parameters.put(Note.TITLE, title);
        if (content != null) parameters.put(Note.CONTENT, content);
        if (creationTime != null) parameters.put(Note.CREATION_TIME, timestamp(creationTime).toString());
        if (modificationTime != null) parameters.put(Note.MODIFICATION_TIME, timestamp(modificationTime).toString());
        if (parameters.isEmpty()) throw new ServiceErrorException("Update parameters cannot be empty");
        final var setters = parameters
                .keySet()
                .stream()
                .map(key -> key + " = ?")
                .collect(Collectors.joining(", "));
        parameters.put(Note.ID, id.toString());
        jdbcTemplate.update(
                "UPDATE note SET " + setters + " WHERE id = ?",
                parameters.values().toArray()
        );
    }

    private RowMapper<Note> mapper() {
        return (resultSet, rowNum) -> NoteFactory.create(resultSet);
    }
}
