package com.github.krystianmuchla.home.domain.id.accessdata.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class AccessDataValidationError extends ValidationError permits
    AccessDataValidationError.NullId,
    AccessDataValidationError.NullUserId,
    AccessDataValidationError.NullLogin,
    AccessDataValidationError.LoginBelowMinLength,
    AccessDataValidationError.LoginAboveMaxLength,
    AccessDataValidationError.NullSalt,
    AccessDataValidationError.SaltBelowMinLength,
    AccessDataValidationError.SaltAboveMaxLength,
    AccessDataValidationError.NullSecret,
    AccessDataValidationError.SecretBelowMinLength,
    AccessDataValidationError.SecretAboveMaxLength,
    AccessDataValidationError.NullCreationTime,
    AccessDataValidationError.NullModificationTime,
    AccessDataValidationError.NullVersion,
    AccessDataValidationError.VersionBelowMinValue {
    public static final class NullId extends AccessDataValidationError {
    }

    public static final class NullUserId extends AccessDataValidationError {
    }

    public static final class NullLogin extends AccessDataValidationError {
    }

    public static final class LoginBelowMinLength extends AccessDataValidationError {
        public final int minLength;

        public LoginBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class LoginAboveMaxLength extends AccessDataValidationError {
        public final int maxLength;

        public LoginAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class NullSalt extends AccessDataValidationError {
    }

    public static final class SaltBelowMinLength extends AccessDataValidationError {
        public final int minLength;

        public SaltBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class SaltAboveMaxLength extends AccessDataValidationError {
        public final int maxLength;

        public SaltAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class NullSecret extends AccessDataValidationError {
    }

    public static final class SecretBelowMinLength extends AccessDataValidationError {
        public final int minLength;

        public SecretBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class SecretAboveMaxLength extends AccessDataValidationError {
        public final int maxLength;

        public SecretAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class NullCreationTime extends AccessDataValidationError {
    }

    public static final class NullModificationTime extends AccessDataValidationError {
    }

    public static final class NullVersion extends AccessDataValidationError {
    }

    public static final class VersionBelowMinValue extends AccessDataValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
