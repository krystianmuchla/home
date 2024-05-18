package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.Config;

import java.time.Duration;

public class NoteGraveCleanerConfig extends Config {
    public static final Duration RATE = Duration.ofDays(1);
    public static final Duration THRESHOLD = Duration.ofDays(30);
}
