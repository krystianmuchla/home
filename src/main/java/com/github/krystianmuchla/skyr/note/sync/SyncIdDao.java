package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.Dao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class SyncIdDao extends Dao {
    private final JdbcTemplate jdbcTemplate;

    public UUID readWithLock() {
        final var result = jdbcTemplate.query(
                "SELECT sync_id FROM note_sync FOR UPDATE",
                (resultSet, rowNum) -> SyncIdFactory.create(resultSet)
        );
        return singleResult(result);
    }

    public boolean update(final UUID syncId) {
        final var result = jdbcTemplate.update("UPDATE note_sync SET sync_id = ?", syncId.toString());
        return isUpdated(result);
    }
}
