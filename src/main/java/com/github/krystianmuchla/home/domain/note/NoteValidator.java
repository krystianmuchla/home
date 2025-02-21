package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationError;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;

public class NoteValidator extends Validator<NoteValidationError, NoteValidationException> {
    private static final int TITLE_MAX_LENGTH = 255;
    private static final int CONTENT_MAX_LENGTH = 65535;
    private static final int VERSION_MIN_VALUE = 1;

    private final Note note;

    public NoteValidator(Note note) {
        this.note = note;
    }

    public NoteValidator checkId() {
        if (note.id == null) {
            errors.add(new NoteValidationError.NullId());
        }
        return this;
    }

    public NoteValidator checkUserId() {
        if (note.userId == null) {
            errors.add(new NoteValidationError.NullUserId());
        }
        return this;
    }

    public NoteValidator checkTitle() {
        return checkTitle(note.title);
    }

    public NoteValidator checkTitle(String title) {
        if (title != null && title.length() > TITLE_MAX_LENGTH) {
            errors.add(new NoteValidationError.TitleAboveMaxLength(TITLE_MAX_LENGTH));
        }
        return this;
    }

    public NoteValidator checkContent() {
        return checkContent(note.content);
    }

    public NoteValidator checkContent(String content) {
        if (content != null && content.length() > CONTENT_MAX_LENGTH) {
            errors.add(new NoteValidationError.ContentAboveMaxLength(CONTENT_MAX_LENGTH));
        }
        return this;
    }

    public NoteValidator checkContentsModificationTime() {
        return checkContentsModificationTime(note.contentsModificationTime);
    }

    public NoteValidator checkContentsModificationTime(Time contentsModificationTime) {
        if (contentsModificationTime == null) {
            errors.add(new NoteValidationError.NullContentsModificationTime());
        }
        return this;
    }

    public NoteValidator checkCreationTime() {
        if (note.creationTime == null) {
            errors.add(new NoteValidationError.NullCreationTime());
        }
        return this;
    }

    public NoteValidator checkModificationTime() {
        return checkModificationTime(note.modificationTime);
    }

    public NoteValidator checkModificationTime(Time modificationTime) {
        if (modificationTime == null) {
            errors.add(new NoteValidationError.NullModificationTime());
        }
        return this;
    }

    public NoteValidator checkVersion() {
        return checkVersion(note.version);
    }

    public NoteValidator checkVersion(Integer version) {
        if (version == null) {
            errors.add(new NoteValidationError.NullVersion());
        } else if (version < VERSION_MIN_VALUE) {
            errors.add(new NoteValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    @Override
    public void validate() throws NoteValidationException {
        if (hasErrors()) {
            throw new NoteValidationException(errors);
        }
    }

    public static void validate(Note note) throws NoteValidationException {
        new NoteValidator(note)
            .checkId()
            .checkUserId()
            .checkTitle()
            .checkContent()
            .checkContentsModificationTime()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
