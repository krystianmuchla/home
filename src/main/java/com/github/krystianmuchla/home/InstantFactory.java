package com.github.krystianmuchla.home;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantFactory {
    public static Instant create() {
        return Instant.now().truncatedTo(ChronoUnit.MILLIS);
    }

    public static Instant create(final Timestamp timestamp) {
        return timestamp.toLocalDateTime().toInstant(ZoneOffset.UTC);
    }
}
