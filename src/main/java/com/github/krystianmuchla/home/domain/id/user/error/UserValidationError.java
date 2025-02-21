package com.github.krystianmuchla.home.domain.id.user.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class UserValidationError extends ValidationError permits
    UserValidationError.NullId,
    UserValidationError.NullName,
    UserValidationError.NameBelowMinLength,
    UserValidationError.NameAboveMaxLength,
    UserValidationError.NullCreationTime,
    UserValidationError.NullModificationTime,
    UserValidationError.NullVersion,
    UserValidationError.VersionBelowMinValue {
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

    public static final class NullCreationTime extends UserValidationError {
    }

    public static final class NullModificationTime extends UserValidationError {
    }

    public static final class NullVersion extends UserValidationError {
    }

    public static final class VersionBelowMinValue extends UserValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
