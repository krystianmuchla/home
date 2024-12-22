package com.github.krystianmuchla.home.domain.drive.file.error;

public abstract sealed class FileValidationError permits
    FileValidationError.NameAboveMaxLength,
    FileValidationError.NameBelowMinLength,
    FileValidationError.NullId,
    FileValidationError.NullName,
    FileValidationError.NullStatus,
    FileValidationError.NullUserId,
    FileValidationError.VersionBelowMinValue {
    public static final class NullId extends FileValidationError {
    }

    public static final class NullUserId extends FileValidationError {
    }

    public static final class NullStatus extends FileValidationError {
    }

    public static final class NullName extends FileValidationError {
    }

    public static final class NameBelowMinLength extends FileValidationError {
        public final int minLength;

        public NameBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class NameAboveMaxLength extends FileValidationError {
        public final int maxLength;

        public NameAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class VersionBelowMinValue extends FileValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
