package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationError;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NoteValidator {
    private static final int TITLE_MAX_LENGTH = 255;
    private static final int CONTENT_MAX_LENGTH = 65535;
    private static final int VERSION_MIN_VALUE = 1;

    public final Set<NoteValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new NoteValidationError.NullId());
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            errors.add(new NoteValidationError.NullUserId());
        }
    }

    public void validateTitle(String title) {
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            errors.add(new NoteValidationError.TitleAboveMaxLength(TITLE_MAX_LENGTH));
        }
    }

    public void validateContent(String content) {
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            errors.add(new NoteValidationError.ContentAboveMaxLength(CONTENT_MAX_LENGTH));
        }
    }

    public void validateContentsModificationTime(Instant contentsModificationTime) {
        if (contentsModificationTime == null) {
            errors.add(new NoteValidationError.NullContentsModificationTime());
        } else if (InstantFactory.create(contentsModificationTime) != contentsModificationTime) {
            errors.add(new NoteValidationError.ContentsModificationTimeWrongFormat());
        }
    }

    public void validateCreationTime(Instant creationTime) {
        if (creationTime != null && InstantFactory.create(creationTime) != creationTime) {
            errors.add(new NoteValidationError.CreationTimeWrongFormat());
        }
    }

    public void validateModificationTime(Instant modificationTime) {
        if (modificationTime != null && InstantFactory.create(modificationTime) != modificationTime) {
            errors.add(new NoteValidationError.ModificationTimeWrongFormat());
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new NoteValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public static void validate(Note note) throws NoteValidationException {
        var validator = new NoteValidator();
        validator.validateId(note.id);
        validator.validateUserId(note.userId);
        validator.validateTitle(note.title);
        validator.validateContent(note.content);
        validator.validateContentsModificationTime(note.contentsModificationTime);
        validator.validateCreationTime(note.creationTime);
        validator.validateModificationTime(note.modificationTime);
        validator.validateVersion(note.version);
        if (validator.hasErrors()) {
            throw new NoteValidationException(validator.errors);
        }
    }
}
