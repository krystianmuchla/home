package com.github.krystianmuchla.home.error.exception.validation;

import java.util.Map;

public record ValidationError(String error, Map<String, Object> details) {
    public ValidationError(final Enum<?> error) {
        this(error, null);
    }

    public ValidationError(final Enum<?> error, Map<String, Object> details) {
        this(error.name(), details);
    }

    public static ValidationError nullValue() {
        return new ValidationError(Error.NULL_VALUE, null);
    }

    public static ValidationError belowValue(final int minValue) {
        return new ValidationError(Error.BELOW_VALUE, Map.of("minValue", minValue));
    }

    public static ValidationError aboveValue(final int maxValue) {
        return new ValidationError(Error.ABOVE_VALUE, Map.of("maxValue", maxValue));
    }

    public static ValidationError belowMinLength(final int minLength) {
        return new ValidationError(Error.BELOW_MIN_LENGTH, Map.of("minLength", minLength));
    }

    public static ValidationError aboveMaxLength(final int maxLength) {
        return new ValidationError(Error.ABOVE_MAX_LENGTH, Map.of("maxLength", maxLength));
    }

    public enum Error {
        NULL_VALUE, BELOW_VALUE, ABOVE_VALUE, BELOW_MIN_LENGTH, ABOVE_MAX_LENGTH
    }
}
