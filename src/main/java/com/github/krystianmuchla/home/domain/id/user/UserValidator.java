package com.github.krystianmuchla.home.domain.id.user;

import com.github.krystianmuchla.home.domain.id.user.error.UserValidationError;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserValidator {
    private static final int NAME_MAX_LENGTH = 100;
    private static final int VERSION_MIN_VALUE = 1;

    public final Set<UserValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new UserValidationError.NullId());
        }
    }

    public void validateName(String name) {
        if (name == null) {
            errors.add(new UserValidationError.NullName());
        } else {
            if (name.isBlank()) {
                errors.add(new UserValidationError.NameBelowMinLength(1));
            }
            if (name.length() > NAME_MAX_LENGTH) {
                errors.add(new UserValidationError.NameAboveMaxLength(NAME_MAX_LENGTH));
            }
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new UserValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public static void validate(User user) throws UserValidationException {
        var validator = new UserValidator();
        validator.validateId(user.id);
        validator.validateName(user.name);
        validator.validateVersion(user.version);
        if (validator.hasErrors()) {
            throw new UserValidationException(validator.errors);
        }
    }
}
