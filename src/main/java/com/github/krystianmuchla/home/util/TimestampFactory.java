package com.github.krystianmuchla.home.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimestampFactory {
    public static Timestamp create(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }
}
