package com.github.krystianmuchla.home.domain.id.user.error;

public abstract sealed class UserValidationError permits UserValidationError.CreationTimeWrongFormat, UserValidationError.ModificationTimeWrongFormat, UserValidationError.NameAboveMaxLength, UserValidationError.NameBelowMinLength, UserValidationError.NullId, UserValidationError.NullName, UserValidationError.VersionBelowMinValue {
    public static final class NullId extends UserValidationError {
    }

    public static final class NullName extends UserValidationError {
    }

    public static final class NameBelowMinLength extends UserValidationError {
        public final int minLength;

        public NameBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class NameAboveMaxLength extends UserValidationError {
        public final int maxLength;

        public NameAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class CreationTimeWrongFormat extends UserValidationError {
    }

    public static final class ModificationTimeWrongFormat extends UserValidationError {
    }

    public static final class VersionBelowMinValue extends UserValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
