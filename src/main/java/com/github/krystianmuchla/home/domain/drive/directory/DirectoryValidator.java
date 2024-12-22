package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationError;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DirectoryValidator {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int VERSION_MIN_VALUE = 1;

    public final Set<DirectoryValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new DirectoryValidationError.NullId());
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            errors.add(new DirectoryValidationError.NullUserId());
        }
    }

    public void validateStatus(DirectoryStatus status) {
        if (status == null) {
            errors.add(new DirectoryValidationError.NullStatus());
        }
    }

    public void validateName(String name) {
        if (name == null) {
            errors.add(new DirectoryValidationError.NullName());
        } else {
            if (name.isBlank()) {
                errors.add(new DirectoryValidationError.NameBelowMinLength(1));
            }
            if (name.length() > NAME_MAX_LENGTH) {
                errors.add(new DirectoryValidationError.NameAboveMaxLength(NAME_MAX_LENGTH));
            }
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new DirectoryValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public static void validate(Directory directory) throws DirectoryValidationException {
        var validator = new DirectoryValidator();
        validator.validateId(directory.id);
        validator.validateUserId(directory.userId);
        validator.validateStatus(directory.status);
        validator.validateName(directory.name);
        validator.validateVersion(directory.version);
        if (validator.hasErrors()) {
            throw new DirectoryValidationException(validator.errors);
        }
    }
}
