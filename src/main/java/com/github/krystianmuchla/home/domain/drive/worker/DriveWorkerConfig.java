package com.github.krystianmuchla.home.domain.drive.worker;

import com.github.krystianmuchla.home.application.Config;

import java.time.Duration;

public class DriveWorkerConfig extends Config {
    public static final Duration RATE = Duration.ofMinutes(60);
}
