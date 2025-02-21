package com.github.krystianmuchla.home.domain.drive.directory.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class DirectoryValidationError extends ValidationError permits
    DirectoryValidationError.NullId,
    DirectoryValidationError.NullUserId,
    DirectoryValidationError.NullStatus,
    DirectoryValidationError.InvalidHierarchy,
    DirectoryValidationError.NullName,
    DirectoryValidationError.NameAboveMaxLength,
    DirectoryValidationError.NameBelowMinLength,
    DirectoryValidationError.NullCreationTime,
    DirectoryValidationError.NullModificationTime,
    DirectoryValidationError.NullVersion,
    DirectoryValidationError.VersionBelowMinValue {
    public static final class NullId extends DirectoryValidationError {
    }

    public static final class NullUserId extends DirectoryValidationError {
    }

    public static final class NullStatus extends DirectoryValidationError {
    }

    public static final class InvalidHierarchy extends DirectoryValidationError {
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

    public static final class NullCreationTime extends DirectoryValidationError {
    }

    public static final class NullModificationTime extends DirectoryValidationError {
    }

    public static final class NullVersion extends DirectoryValidationError {
    }

    public static final class VersionBelowMinValue extends DirectoryValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
