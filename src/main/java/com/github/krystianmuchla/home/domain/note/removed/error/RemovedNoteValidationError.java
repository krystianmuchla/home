package com.github.krystianmuchla.home.domain.note.removed.error;

public abstract sealed class RemovedNoteValidationError permits RemovedNoteValidationError.CreationTimeWrongFormat, RemovedNoteValidationError.ModificationTimeWrongFormat, RemovedNoteValidationError.NullId, RemovedNoteValidationError.NullRemovalTime, RemovedNoteValidationError.NullUserId, RemovedNoteValidationError.RemovalTimeWrongFormat, RemovedNoteValidationError.VersionBelowMinValue {
    public static final class NullId extends RemovedNoteValidationError {
    }

    public static final class NullUserId extends RemovedNoteValidationError {
    }

    public static final class NullRemovalTime extends RemovedNoteValidationError {
    }

    public static final class RemovalTimeWrongFormat extends RemovedNoteValidationError {
    }

    public static final class CreationTimeWrongFormat extends RemovedNoteValidationError {
    }

    public static final class ModificationTimeWrongFormat extends RemovedNoteValidationError {
    }

    public static final class VersionBelowMinValue extends RemovedNoteValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
