package com.github.krystianmuchla.home.application.exception;

import com.github.krystianmuchla.home.application.util.InstantFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Validator {
    private static final int VERSION_MIN_VALUE = 1;

    public static List<ValidationError> validateUuid(String uuid) {
        if (uuid == null) {
            return List.of(ValidationError.nullValue());
        }
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException exception) {
            return List.of(ValidationError.wrongFormat());
        }
        return List.of();
    }

    public static List<ValidationError> validateUuid(UUID uuid) {
        if (uuid == null) {
            return List.of(ValidationError.nullValue());
        }
        return List.of();
    }

    public static List<ValidationError> validateInstant(Instant instant) {
        if (instant == null) {
            return List.of(ValidationError.nullValue());
        }
        if (InstantFactory.create(instant) != instant) {
            return List.of(ValidationError.wrongFormat());
        }
        return List.of();
    }

    public static List<ValidationError> validateCreationTime(Instant creationTime) {
        return validateInstant(creationTime);
    }

    public static List<ValidationError> validateModificationTime(Instant modificationTime) {
        return validateInstant(modificationTime);
    }

    public static List<ValidationError> validateVersion(Integer version) {
        if (version == null) {
            return List.of(ValidationError.nullValue());
        }
        if (version < VERSION_MIN_VALUE) {
            return List.of(ValidationError.belowMinValue(VERSION_MIN_VALUE));
        }
        return List.of();
    }
}
