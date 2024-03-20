package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.ConnectionManager;
import com.github.krystianmuchla.home.db.Transaction;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class NoteGraveCleaner implements Runnable {
    private final boolean enabled;
    private final Duration rate;
    private final Duration threshold;

    public NoteGraveCleaner(final Boolean enabled, final Duration rate, final Duration threshold) {
        this.enabled = Objects.requireNonNullElse(enabled, true);
        this.rate = Objects.requireNonNullElse(rate, Duration.ofDays(1));
        this.threshold = Objects.requireNonNullElse(threshold, Duration.ofDays(30));
    }

    @Override
    @SneakyThrows
    public void run() {
        ConnectionManager.registerConnection();
        while (enabled) {
            final var creationTimeThreshold = InstantFactory.create().minus(threshold.toMillis(), ChronoUnit.MILLIS);
            Transaction.run(() -> NoteGraveSql.delete(creationTimeThreshold));
            try {
                Thread.sleep(rate);
            } catch (final InterruptedException ignored) {
                break;
            }
        }
    }
}
