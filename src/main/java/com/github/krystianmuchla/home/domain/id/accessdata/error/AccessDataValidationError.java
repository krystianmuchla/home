package com.github.krystianmuchla.home.domain.id.accessdata.error;

public abstract sealed class AccessDataValidationError permits AccessDataValidationError.CreationTimeWrongFormat, AccessDataValidationError.LoginAboveMaxLength, AccessDataValidationError.LoginBelowMinLength, AccessDataValidationError.ModificationTimeWrongFormat, AccessDataValidationError.NullId, AccessDataValidationError.NullLogin, AccessDataValidationError.NullSalt, AccessDataValidationError.NullSecret, AccessDataValidationError.NullUserId, AccessDataValidationError.SaltAboveMaxLength, AccessDataValidationError.SaltBelowMinLength, AccessDataValidationError.SecretAboveMaxLength, AccessDataValidationError.SecretBelowMinLength, AccessDataValidationError.VersionBelowMinValue {
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

    public static final class CreationTimeWrongFormat extends AccessDataValidationError {
    }

    public static final class ModificationTimeWrongFormat extends AccessDataValidationError {
    }

    public static final class VersionBelowMinValue extends AccessDataValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
