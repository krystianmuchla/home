package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.Dao;
import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.note.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoteGraveDao extends Dao {
    private final JdbcTemplate jdbcTemplate;

    public void create(final UUID id) {
        create(id, InstantFactory.create());
    }

    public void create(final Note note) {
        create(note.id(), note.modificationTime());
    }

    private void create(final UUID id, final Instant creationTime) {
        jdbcTemplate.update(
            "INSERT INTO note_grave VALUES (?, ?)",
            id.toString(),
            timestamp(creationTime).toString()
        );
    }

    public List<NoteGrave> read() {
        return jdbcTemplate.query("SELECT * FROM note_grave", mapper());
    }

    public void delete(final Instant creationTimeThreshold) {
        jdbcTemplate.update(
                "DELETE FROM note_grave WHERE creation_time < ?",
                timestamp(creationTimeThreshold).toString()
        );
    }

    public void delete(final UUID id) {
        jdbcTemplate.update(
                "DELETE FROM note_grave WHERE id = ?",
                id.toString()
        );
    }

    private RowMapper<NoteGrave> mapper() {
        return (resultSet, rowNum) -> NoteGraveFactory.create(resultSet);
    }
}
