package com.github.krystianmuchla.home.domain.note.removed;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationError;
import com.github.krystianmuchla.home.domain.note.removed.error.RemovedNoteValidationException;

public class RemovedNoteValidator extends Validator<RemovedNoteValidationError, RemovedNoteValidationException> {
    private static final int VERSION_MIN_VALUE = 1;

    private final RemovedNote removedNote;

    public RemovedNoteValidator(RemovedNote removedNote) {
        this.removedNote = removedNote;
    }

    public RemovedNoteValidator checkId() {
        if (removedNote.id == null) {
            errors.add(new RemovedNoteValidationError.NullId());
        }
        return this;
    }

    public RemovedNoteValidator checkUserId() {
        if (removedNote.userId == null) {
            errors.add(new RemovedNoteValidationError.NullUserId());
        }
        return this;
    }

    public RemovedNoteValidator checkRemovalTime() {
        return checkRemovalTime(removedNote.removalTime);
    }

    public RemovedNoteValidator checkRemovalTime(Time removalTime) {
        if (removalTime == null) {
            errors.add(new RemovedNoteValidationError.NullRemovalTime());
        }
        return this;
    }

    public RemovedNoteValidator checkCreationTime() {
        if (removedNote.creationTime == null) {
            errors.add(new RemovedNoteValidationError.NullCreationTime());
        }
        return this;
    }

    public RemovedNoteValidator checkModificationTime() {
        return checkModificationTime(removedNote.modificationTime);
    }

    public RemovedNoteValidator checkModificationTime(Time modificationTime) {
        if (modificationTime == null) {
            errors.add(new RemovedNoteValidationError.NullModificationTime());
        }
        return this;
    }

    public RemovedNoteValidator checkVersion() {
        return checkVersion(removedNote.version);
    }

    public RemovedNoteValidator checkVersion(Integer version) {
        if (version == null) {
            errors.add(new RemovedNoteValidationError.NullVersion());
        } else if (version < VERSION_MIN_VALUE) {
            errors.add(new RemovedNoteValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    @Override
    public void validate() throws RemovedNoteValidationException {
        if (hasErrors()) {
            throw new RemovedNoteValidationException(errors);
        }
    }

    public static void validate(RemovedNote removedNote) throws RemovedNoteValidationException {
        new RemovedNoteValidator(removedNote)
            .checkId()
            .checkUserId()
            .checkRemovalTime()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
