package com.github.krystianmuchla.home.infrastructure.http.core.error;

import java.util.Map;

public record ValidationError(String error, Map<String, Object> details) {
    public ValidationError(Enum<?> error, Map<String, Object> details) {
        this(error.name(), details);
    }

    public static ValidationError nullValue() {
        return new ValidationError(Error.NULL_VALUE, null);
    }

    public static ValidationError wrongFormat() {
        return new ValidationError(Error.WRONG_FORMAT, null);
    }

    public static ValidationError belowMinLength(int minLength) {
        return new ValidationError(Error.BELOW_MIN_LENGTH, Map.of("minLength", minLength));
    }

    public static ValidationError aboveMaxLength(int maxLength) {
        return new ValidationError(Error.ABOVE_MAX_LENGTH, Map.of("maxLength", maxLength));
    }

    public static ValidationError belowMinValue(int minValue) {
        return new ValidationError(Error.BELOW_MIN_VALUE, Map.of("minValue", minValue));
    }

    public enum Error {
        NULL_VALUE, WRONG_FORMAT, BELOW_MIN_LENGTH, ABOVE_MAX_LENGTH, BELOW_MIN_VALUE
    }
}
