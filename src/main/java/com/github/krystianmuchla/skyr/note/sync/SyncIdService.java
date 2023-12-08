package com.github.krystianmuchla.skyr.note.sync;

import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyncIdService {
    private final SyncIdDao syncIdDao;

    public UUID getWithLock() {
        final var result = syncIdDao.readWithLock();
        if (result == null) throw new ServerErrorException("Sync id not found");
        return result;
    }

    public void update(final UUID syncId) {
        final var result = syncIdDao.update(syncId);
        if (!result) throw new ServerErrorException("Sync id not found");
    }
}
