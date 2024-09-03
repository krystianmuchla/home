package com.github.krystianmuchla.home.note.removed.worker;

import com.github.krystianmuchla.home.Config;

import java.time.Duration;

public class RemovedNoteWorkerConfig extends Config {
    public static final Duration RATE = Duration.ofDays(1);
    public static final Duration THRESHOLD = Duration.ofDays(30);
}
