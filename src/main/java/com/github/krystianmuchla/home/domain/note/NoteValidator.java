package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.exception.Validator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class NoteValidator extends Validator {
    private static final int NOTE_TITLE_MAX_LENGTH = 255;
    private static final int NOTE_CONTENT_MAX_LENGTH = 65535;

    public static List<ValidationError> validateNoteId(UUID noteId) {
        return validateUuid(noteId);
    }

    public static List<ValidationError> validateNoteTitle(String noteTitle) {
        if (noteTitle == null) {
            return List.of(ValidationError.nullValue());
        }
        if (noteTitle.length() > NOTE_TITLE_MAX_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(NOTE_TITLE_MAX_LENGTH));
        }
        return List.of();
    }

    public static List<ValidationError> validateNoteContent(String noteContent) {
        if (noteContent == null) {
            return List.of(ValidationError.nullValue());
        }
        if (noteContent.length() > NOTE_CONTENT_MAX_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(NOTE_CONTENT_MAX_LENGTH));
        }
        return List.of();
    }

    public static List<ValidationError> validateNoteContentsModificationTime(Instant noteContentsModificationTime) {
        return validateInstant(noteContentsModificationTime);
    }

    public static List<ValidationError> validateRemovalTime(Instant removalTime) {
        return validateInstant(removalTime);
    }
}
