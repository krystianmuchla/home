package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.util.InstantFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class NoteGraveCleaner implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(NoteGraveCleaner.class);
    private final Duration rate = NoteGraveCleanerConfig.RATE;
    private final Duration threshold = NoteGraveCleanerConfig.THRESHOLD;

    @Override
    public void run() {
        try {
            while (true) {
                var creationTimeThreshold = InstantFactory.create().minus(threshold.toMillis(), ChronoUnit.MILLIS);
                Transaction.run(() -> NoteGravePersistence.delete(creationTimeThreshold));
                try {
                    Thread.sleep(rate);
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        } catch (Exception exception) {
            LOG.warn("{}", exception.getMessage(), exception);
        }
    }
}
