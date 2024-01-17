package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.DbConnection;
import com.github.krystianmuchla.home.db.Transactional;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

public class NoteGraveCleaner implements Runnable, Transactional {
    private final Connection dbConnection;
    private final NoteGraveDao noteGraveDao;
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
        dbConnection = DbConnection.create();
        noteGraveDao = NoteGraveDao.getInstance(dbConnection);
        this.enabled = Objects.requireNonNullElse(enabled, true);
        this.rate = Objects.requireNonNullElse(rate, 1);
        this.rateUnit = Objects.requireNonNullElse(rateUnit, ChronoUnit.DAYS);
        this.threshold = Objects.requireNonNullElse(threshold, 30);
        this.thresholdUnit = Objects.requireNonNullElse(thresholdUnit, ChronoUnit.DAYS);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (enabled) {
            final var creationTimeThreshold = InstantFactory.create().minus(threshold, thresholdUnit);
            transactional(dbConnection, () -> {
                noteGraveDao.delete(creationTimeThreshold);
            });
            try {
                Thread.sleep(Duration.of(rate, rateUnit));
            } catch (final InterruptedException ignored) {
                break;
            }
        }
    }
}
