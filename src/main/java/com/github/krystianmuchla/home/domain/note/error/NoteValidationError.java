package com.github.krystianmuchla.home.domain.note.error;

public abstract sealed class NoteValidationError permits NoteValidationError.ContentAboveMaxLength, NoteValidationError.ContentsModificationTimeWrongFormat, NoteValidationError.CreationTimeWrongFormat, NoteValidationError.ModificationTimeWrongFormat, NoteValidationError.NullContentsModificationTime, NoteValidationError.NullId, NoteValidationError.NullUserId, NoteValidationError.TitleAboveMaxLength, NoteValidationError.VersionBelowMinValue {
    public static final class NullId extends NoteValidationError {
    }

    public static final class NullUserId extends NoteValidationError {
    }

    public static final class TitleAboveMaxLength extends NoteValidationError {
        public final int maxLength;

        public TitleAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class ContentAboveMaxLength extends NoteValidationError {
        public final int maxLength;

        public ContentAboveMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }
    }

    public static final class NullContentsModificationTime extends NoteValidationError {
    }

    public static final class ContentsModificationTimeWrongFormat extends NoteValidationError {
    }

    public static final class CreationTimeWrongFormat extends NoteValidationError {
    }

    public static final class ModificationTimeWrongFormat extends NoteValidationError {
    }

    public static final class VersionBelowMinValue extends NoteValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
