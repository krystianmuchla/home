package com.github.krystianmuchla.home.domain.note.removed.worker;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.worker.Worker;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
import com.github.krystianmuchla.home.infrastructure.persistence.note.RemovedNotePersistence;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RemovedNoteWorker extends Worker {
    private final Duration threshold = RemovedNoteWorkerConfig.THRESHOLD;

    public RemovedNoteWorker() {
        super(RemovedNoteWorkerConfig.RATE);
    }

    @Override
    protected void work() {
        var creationTimeThreshold = new Time().minus(threshold.toMillis(), ChronoUnit.MILLIS);
        Transaction.run(() -> RemovedNotePersistence.delete(creationTimeThreshold));
    }
}
