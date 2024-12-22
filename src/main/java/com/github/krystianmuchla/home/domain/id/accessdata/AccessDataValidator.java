package com.github.krystianmuchla.home.domain.id.accessdata;

import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationError;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AccessDataValidator {
    private static final int LOGIN_MAX_LENGTH = 50;
    private static final int SALT_LENGTH = 32;
    private static final int SECRET_LENGTH = 32;
    private static final int VERSION_MIN_VALUE = 1;

    private final Set<AccessDataValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new AccessDataValidationError.NullId());
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            errors.add(new AccessDataValidationError.NullUserId());
        }
    }

    public void validateLogin(String login) {
        if (login == null) {
            errors.add(new AccessDataValidationError.NullLogin());
        } else {
            if (login.isEmpty()) {
                errors.add(new AccessDataValidationError.LoginBelowMinLength(1));
            }
            if (login.length() > LOGIN_MAX_LENGTH) {
                errors.add(new AccessDataValidationError.LoginAboveMaxLength(LOGIN_MAX_LENGTH));
            }
        }
    }

    public void validateSalt(byte[] salt) {
        if (salt == null) {
            errors.add(new AccessDataValidationError.NullSalt());
        } else {
            if (salt.length < SALT_LENGTH) {
                errors.add(new AccessDataValidationError.SaltBelowMinLength(SALT_LENGTH));
            }
            if (salt.length > SALT_LENGTH) {
                errors.add(new AccessDataValidationError.SaltAboveMaxLength(SALT_LENGTH));
            }
        }
    }

    public void validateSecret(byte[] secret) {
        if (secret == null) {
            errors.add(new AccessDataValidationError.NullSecret());
        } else {
            if (secret.length < SECRET_LENGTH) {
                errors.add(new AccessDataValidationError.SecretBelowMinLength(SECRET_LENGTH));
            }
            if (secret.length > SECRET_LENGTH) {
                errors.add(new AccessDataValidationError.SecretAboveMaxLength(SECRET_LENGTH));
            }
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new AccessDataValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public static void validate(AccessData accessData) throws AccessDataValidationException {
        var validator = new AccessDataValidator();
        validator.validateId(accessData.id);
        validator.validateUserId(accessData.userId);
        validator.validateLogin(accessData.login);
        validator.validateSalt(accessData.salt);
        validator.validateSecret(accessData.secret);
        validator.validateVersion(accessData.version);
        if (validator.hasErrors()) {
            throw new AccessDataValidationException(validator.errors);
        }
    }
}
