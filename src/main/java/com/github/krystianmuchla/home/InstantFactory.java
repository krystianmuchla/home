package com.github.krystianmuchla.home;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class InstantFactory {
    private InstantFactory() {
    }

    public static Instant create() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static Instant create(final Timestamp timestamp) {
        return timestamp.toLocalDateTime().toInstant(ZoneOffset.UTC);
    }
}
