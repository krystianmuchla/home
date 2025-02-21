package com.github.krystianmuchla.home.domain.note.error;

import com.github.krystianmuchla.home.domain.core.error.ValidationError;

public abstract sealed class NoteValidationError extends ValidationError permits
    NoteValidationError.NullId,
    NoteValidationError.NullUserId,
    NoteValidationError.TitleAboveMaxLength,
    NoteValidationError.ContentAboveMaxLength,
    NoteValidationError.NullContentsModificationTime,
    NoteValidationError.NullCreationTime,
    NoteValidationError.NullModificationTime,
    NoteValidationError.NullVersion,
    NoteValidationError.VersionBelowMinValue {
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

    public static final class NullCreationTime extends NoteValidationError {
    }

    public static final class NullModificationTime extends NoteValidationError {
    }

    public static final class NullVersion extends NoteValidationError {
    }

    public static final class VersionBelowMinValue extends NoteValidationError {
        public final int minValue;

        public VersionBelowMinValue(int minValue) {
            this.minValue = minValue;
        }
    }
}
