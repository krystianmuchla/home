package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationError;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RemovedNoteValidator {
    private static final int VERSION_MIN_VALUE = 1;

    public final Set<RemovedNoteValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new RemovedNoteValidationError.NullId());
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            errors.add(new RemovedNoteValidationError.NullUserId());
        }
    }

    public void validateRemovalTime(Time removalTime) {
        if (removalTime == null) {
            errors.add(new RemovedNoteValidationError.NullRemovalTime());
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new RemovedNoteValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public static void validate(RemovedNote removedNote) throws RemovedNoteValidationException {
        var validator = new RemovedNoteValidator();
        validator.validateId(removedNote.id);
        validator.validateUserId(removedNote.userId);
        validator.validateRemovalTime(removedNote.removalTime);
        validator.validateVersion(removedNote.version);
        if (validator.hasErrors()) {
            throw new RemovedNoteValidationException(validator.errors);
        }
    }
}
