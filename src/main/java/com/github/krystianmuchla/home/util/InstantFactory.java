package com.github.krystianmuchla.home.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class InstantFactory {
    public static Instant create() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static Instant create(final Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime().toInstant(ZoneOffset.UTC);
    }
}
