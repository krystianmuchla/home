package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class NoteGraveCleaner implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(NoteGraveCleaner.class);
    private final boolean enabled = NoteGraveCleanerConfig.ENABLED;
    private final Duration rate = NoteGraveCleanerConfig.RATE;
    private final Duration threshold = NoteGraveCleanerConfig.THRESHOLD;

    @Override
    public void run() {
        if (!enabled) {
            return;
        }
        try {
            while (true) {
                final var creationTimeThreshold = InstantFactory.create().minus(threshold.toMillis(), ChronoUnit.MILLIS);
                Transaction.run(() -> NoteGraveSql.delete(creationTimeThreshold));
                try {
                    Thread.sleep(rate);
                } catch (final InterruptedException ignored) {
                    break;
                }
            }
        } catch (final Exception exception) {
            LOG.error("{}", exception.getMessage(), exception);
        }
    }
}
