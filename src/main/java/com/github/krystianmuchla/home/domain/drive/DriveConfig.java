package com.github.krystianmuchla.home.domain.drive;

import com.github.krystianmuchla.home.application.Config;

public class DriveConfig extends Config {
    public static final String LOCATION;

    static {
        var location = resolve("drive.location", "HOME_DRIVE_LOCATION");
        if (location == null) {
            throw new IllegalStateException("Drive location is not specified");
        }
        LOCATION = location;
    }
}
