package com.github.krystianmuchla.home.domain.note.removed.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class RemovedNoteValidationError extends ValidationError permits
    RemovedNoteValidationError.NullId,
    RemovedNoteValidationError.NullUserId,
    RemovedNoteValidationError.NullRemovalTime,
    RemovedNoteValidationError.NullCreationTime,
    RemovedNoteValidationError.NullModificationTime,
    RemovedNoteValidationError.NullVersion,
    RemovedNoteValidationError.VersionBelowMinValue {
    public static final class NullId extends RemovedNoteValidationError {
    }

    public static final class NullUserId extends RemovedNoteValidationError {
    }

    public static final class NullRemovalTime extends RemovedNoteValidationError {
    }

    public static final class NullCreationTime extends RemovedNoteValidationError {
    }

    public static final class NullModificationTime extends RemovedNoteValidationError {
    }

    public static final class NullVersion extends RemovedNoteValidationError {
    }

    public static final class VersionBelowMinValue extends RemovedNoteValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
