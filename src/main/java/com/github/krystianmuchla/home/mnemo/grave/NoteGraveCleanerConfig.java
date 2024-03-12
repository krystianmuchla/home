package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.Config;

import java.time.Duration;

public class NoteGraveCleanerConfig extends Config {
    public static final Boolean ENABLED;
    public static final Duration RATE;
    public static final Duration THRESHOLD;

    static {
        final var enabled = resolve("note-grave-cleaner.enabled", "NOTE_GRAVE_CLEANER_ENABLED");
        ENABLED = enabled == null ? null : Boolean.valueOf(enabled);
        final var rate = resolve("note-grave-cleaner.rate", "NOTE_GRAVE_CLEANER_RATE");
        RATE = rate == null ? null : Duration.parse(rate);
        final var threshold = resolve("note-grave-cleaner.threshold", "NOTE_GRAVE_CLEANER_THRESHOLD");
        THRESHOLD = threshold == null ? null : Duration.parse(threshold);
    }
}
