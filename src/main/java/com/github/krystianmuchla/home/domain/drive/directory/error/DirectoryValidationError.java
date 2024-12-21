package com.github.krystianmuchla.home.domain.drive.directory.error;

public abstract sealed class DirectoryValidationError
    permits DirectoryValidationError.CreationTimeWrongFormat,
    DirectoryValidationError.ModificationTimeWrongFormat,
    DirectoryValidationError.NameAboveMaxLength,
    DirectoryValidationError.NameBelowMinLength,
    DirectoryValidationError.NullId,
    DirectoryValidationError.NullName,
    DirectoryValidationError.NullStatus,
    DirectoryValidationError.NullUserId,
    DirectoryValidationError.VersionBelowMinValue {
    public static final class NullId extends DirectoryValidationError {
    }

    public static final class NullUserId extends DirectoryValidationError {
    }

    public static final class NullStatus extends DirectoryValidationError {
    }

    public static final class NullName extends DirectoryValidationError {
    }

    public static final class NameBelowMinLength extends DirectoryValidationError {
        public final int minLength;

        public NameBelowMinLength(int minLength) {
            this.minLength = minLength;
        }
    }

    public static final class NameAboveMaxLength extends DirectoryValidationError {
        public final int maxLength;

        public NameAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class CreationTimeWrongFormat extends DirectoryValidationError {
    }

    public static final class ModificationTimeWrongFormat extends DirectoryValidationError {
    }

    public static final class VersionBelowMinValue extends DirectoryValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
