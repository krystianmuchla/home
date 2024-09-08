package com.github.krystianmuchla.home.drive.worker;

import com.github.krystianmuchla.home.Config;

import java.time.Duration;

public class DriveWorkerConfig extends Config {
    public static final Duration RATE = Duration.ofMinutes(60);
}
