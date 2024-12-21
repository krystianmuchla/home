package com.github.krystianmuchla.home.domain.id.session;

import com.github.krystianmuchla.home.domain.id.session.error.SessionValidationError;
import com.github.krystianmuchla.home.domain.id.session.error.SessionValidationException;
import com.github.krystianmuchla.home.domain.id.user.User;

import java.util.HashSet;
import java.util.Set;

public class SessionValidator {
    public final Set<SessionValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateUser(User user) {
        if (user == null) {
            errors.add(new SessionValidationError.NullUser());
        }
    }

    public static void validate(Session session) throws SessionValidationException {
        var validator = new SessionValidator();
        validator.validateUser(session.user);
        if (validator.hasErrors()) {
            throw new SessionValidationException(validator.errors);
        }
    }
}
