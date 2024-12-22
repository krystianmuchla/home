package com.github.krystianmuchla.home.application.time;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public record Time(Instant value) {
    public Time {
        if (value == null) {
            throw new IllegalArgumentException("Time value cannot be null");
        }
        if (value != value.truncatedTo(ChronoUnit.MILLIS)) {
            throw new IllegalArgumentException("Time value has invalid precision");
        }
    }

    public Time() {
        this(Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }

    public Time minus(long amount, TemporalUnit unit) {
        return new Time(value.minus(amount, unit));
    }

    public boolean isBefore(Time time) {
        return value.isBefore(time.value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
