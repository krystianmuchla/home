package com.example.skyr.note.sync;

import com.example.skyr.Dao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class NotesSyncDao extends Dao {
    private final JdbcTemplate jdbcTemplate;

    public NotesSync readWithLock() {
        final var result = jdbcTemplate.query(
                "SELECT sync_id, sync_time FROM notes_sync FOR UPDATE",
                (resultSet, rowNum) -> NotesSyncFactory.create(resultSet)
        );
        return singleResult(result);
    }

    public void update(final NotesSync notesSync) {
        jdbcTemplate.update(
                "UPDATE notes_sync SET sync_id = ?, sync_time = ?",
                notesSync.syncId().toString(),
                timestamp(notesSync.syncTime()).toString()
        );
    }
}
