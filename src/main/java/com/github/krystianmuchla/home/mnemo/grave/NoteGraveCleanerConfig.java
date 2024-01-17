package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoteGraveCleanerConfig extends Config {
    public static final Boolean ENABLED;
    public static final Integer RATE;
    public static final ChronoUnit RATE_UNIT;
    public static final Integer THRESHOLD;
    public static final ChronoUnit THRESHOLD_UNIT;

    static {
        final var enabled = resolve("note-grave-cleaner.enabled", "NOTE_GRAVE_CLEANER_ENABLED");
        ENABLED = enabled == null ? null : Boolean.valueOf(enabled);
        final var rate = resolve("note-grave-cleaner.rate", "NOTE_GRAVE_CLEANER_RATE");
        RATE = rate == null ? null : Integer.valueOf(rate);
        final var rateUnit = resolve("note-grave-cleaner.rate-unit", "NOTE_GRAVE_CLEANER_RATE_UNIT");
        RATE_UNIT = rateUnit == null ? null : ChronoUnit.valueOf(rateUnit);
        final var threshold = resolve("note-grave-cleaner.threshold", "NOTE_GRAVE_CLEANER_THRESHOLD");
        THRESHOLD = threshold == null ? null : Integer.valueOf(threshold);
        final var thresholdUnit = resolve("note-grave-cleaner.threshold-unit", "NOTE_GRAVE_CLEANER_THRESHOLD_UNIT");
        THRESHOLD_UNIT = thresholdUnit == null ? null : ChronoUnit.valueOf(thresholdUnit);
    }
}
