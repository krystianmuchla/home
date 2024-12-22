package com.github.krystianmuchla.home.application.time;

import java.sql.Timestamp;
import java.time.Instant;

public class TimeFactory {
    public static Time create(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Time(timestamp.toInstant());
    }

    public static Time create(String string) {
        if (string == null) {
            return null;
        }
        return new Time(Instant.parse(string));
    }
}
