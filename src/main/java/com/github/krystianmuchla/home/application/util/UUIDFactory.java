package com.github.krystianmuchla.home.application.util;

import java.util.UUID;

public class UUIDFactory {
    public static UUID create(String uuid) {
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid);
    }
}
