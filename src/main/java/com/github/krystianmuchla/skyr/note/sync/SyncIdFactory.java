package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SyncIdFactory {
    public static UUID create(final ResultSet resultSet) {
        final UUID syncId;
        try {
            syncId = UUID.fromString(resultSet.getString("sync_id"));
        } catch (final SQLException exception) {
            throw new ServerErrorException(exception);
        }
        return syncId;
    }
}
