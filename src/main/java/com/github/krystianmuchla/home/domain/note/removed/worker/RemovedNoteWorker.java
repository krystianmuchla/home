package com.github.krystianmuchla.home.domain.note.removed.worker;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.worker.Worker;
import com.github.krystianmuchla.home.domain.note.removed.RemovedNotePersistence;
import com.github.krystianmuchla.home.infrastructure.persistence.Transaction;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RemovedNoteWorker extends Worker {
    private final Duration threshold = RemovedNoteWorkerConfig.THRESHOLD;

    public RemovedNoteWorker() {
        super(RemovedNoteWorkerConfig.RATE);
    }

    @Override
    protected void work() {
        var creationTimeThreshold = InstantFactory.create().minus(threshold.toMillis(), ChronoUnit.MILLIS);
        Transaction.run(() -> RemovedNotePersistence.delete(creationTimeThreshold));
    }
}
