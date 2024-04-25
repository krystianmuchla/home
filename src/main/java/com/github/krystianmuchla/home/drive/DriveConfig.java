package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.Config;
import com.github.krystianmuchla.home.error.exception.InternalException;

public class DriveConfig extends Config {
    public static final String LOCATION;

    static {
        final var location = resolve("drive.location", "HOME_DRIVE_LOCATION");
        if (location == null) {
            throw new InternalException("Drive location is not specified");
        }
        LOCATION = location;
    }
}
