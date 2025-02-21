package com.github.krystianmuchla.home.domain.id.password.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class PasswordValidationError extends ValidationError permits
    PasswordValidationError.NullValue,
    PasswordValidationError.ValueBelowMinLength,
    PasswordValidationError.ValueAboveMaxLength,
    PasswordValidationError.ValueWrongFormat {
    public static final class NullValue extends PasswordValidationError {
    }

    public static final class ValueBelowMinLength extends PasswordValidationError {
        public final int minLength;

        public ValueBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class ValueAboveMaxLength extends PasswordValidationError {
        public final int maxLength;

        public ValueAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class ValueWrongFormat extends PasswordValidationError {
    }
}
