package com.github.krystianmuchla.home.application.util;

import com.github.krystianmuchla.home.application.time.Time;

import java.sql.Timestamp;

public class TimestampFactory {
    public static Timestamp create(Time time) {
        if (time == null) {
            return null;
        }
        return Timestamp.from(time.value());
    }
}
