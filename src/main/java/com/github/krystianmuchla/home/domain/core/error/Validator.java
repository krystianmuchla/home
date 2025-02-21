package com.github.krystianmuchla.home.domain.core.error;

import java.util.HashSet;
import java.util.Set;

public abstract class Validator<E extends ValidationError, T extends ValidationException> {
    public final Set<E> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public abstract void validate() throws T;
}
