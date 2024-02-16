package com.github.krystianmuchla.home.mnemo.grave;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.ConnectionManager;
import com.github.krystianmuchla.home.db.Transaction;

import lombok.SneakyThrows;

public class NoteGraveCleaner implements Runnable {
    private final NoteGraveDao noteGraveDao = NoteGraveDao.INSTANCE;
    private boolean enabled;
    private final int rate;
    private final TemporalUnit rateUnit;
    private final int threshold;
    private final TemporalUnit thresholdUnit;

    public NoteGraveCleaner(
        final Boolean enabled,
        final Integer rate,
        final ChronoUnit rateUnit,
        final Integer threshold,
        final ChronoUnit thresholdUnit
    ) {
        this.enabled = Objects.requireNonNullElse(enabled, true);
        this.rate = Objects.requireNonNullElse(rate, 1);
        this.rateUnit = Objects.requireNonNullElse(rateUnit, ChronoUnit.DAYS);
        this.threshold = Objects.requireNonNullElse(threshold, 30);
        this.thresholdUnit = Objects.requireNonNullElse(thresholdUnit, ChronoUnit.DAYS);
    }

    @Override
    @SneakyThrows
    public void run() {
        ConnectionManager.register();
        while (enabled) {
            final var creationTimeThreshold = InstantFactory.create().minus(threshold, thresholdUnit);
            Transaction.run(() -> noteGraveDao.delete(creationTimeThreshold));
            try {
                Thread.sleep(Duration.of(rate, rateUnit));
            } catch (final InterruptedException ignored) {
                break;
            }
        }
    }
}
