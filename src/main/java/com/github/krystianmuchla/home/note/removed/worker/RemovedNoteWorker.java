package com.github.krystianmuchla.home.note.removed.worker;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.note.removed.RemovedNotePersistence;
import com.github.krystianmuchla.home.util.InstantFactory;
import com.github.krystianmuchla.home.worker.Worker;

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
