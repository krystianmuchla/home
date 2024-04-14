package com.github.krystianmuchla.home.mnemo.grave;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.error.exception.InternalException;

import java.time.Duration;

public class NoteGraveCleanerConfig extends Config {
    public static final boolean ENABLED;
    public static final Duration RATE = Duration.ofDays(1);
    public static final Duration THRESHOLD = Duration.ofDays(30);

    static {
        final var enabled = resolve("note-grave-cleaner.enabled", "NOTE_GRAVE_CLEANER_ENABLED");
        if (enabled == null) {
            throw new InternalException("Note grave cleaner enabled flag not specified");
        }
        ENABLED = Boolean.parseBoolean(enabled);
    }
}
