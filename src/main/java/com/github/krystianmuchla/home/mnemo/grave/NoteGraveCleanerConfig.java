package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.Config;

import java.time.Duration;

public class NoteGraveCleanerConfig extends Config {
    public static final Boolean ENABLED;
    public static final Duration RATE = Duration.ofDays(1);
    public static final Duration THRESHOLD = Duration.ofDays(30);

    static {
        final var enabled = resolve("note-grave-cleaner.enabled", "NOTE_GRAVE_CLEANER_ENABLED");
        ENABLED = enabled == null ? null : Boolean.valueOf(enabled);
    }
}
