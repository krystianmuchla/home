package com.github.krystianmuchla.home.domain.note.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;

import java.util.Collection;

public class NoteValidationException extends DomainException {
    public final Collection<NoteValidationError> errors;

    public NoteValidationException(Collection<NoteValidationError> errors) {
        this.errors = errors;
    }
}
