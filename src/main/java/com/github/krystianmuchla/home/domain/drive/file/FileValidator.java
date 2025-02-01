package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationError;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileValidator {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int VERSION_MIN_VALUE = 1;

    public final Set<FileValidationError> errors = new HashSet<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void validateId(UUID id) {
        if (id == null) {
            errors.add(new FileValidationError.NullId());
        }
    }

    public void validateUserId(UUID userId) {
        if (userId == null) {
            errors.add(new FileValidationError.NullUserId());
        }
    }

    public void validateStatus(FileStatus status) {
        if (status == null) {
            errors.add(new FileValidationError.NullStatus());
        }
    }

    public void validateName(String name) {
        if (name == null) {
            errors.add(new FileValidationError.NullName());
        } else {
            if (name.isBlank()) {
                errors.add(new FileValidationError.NameBelowMinLength(1));
            }
            if (name.length() > NAME_MAX_LENGTH) {
                errors.add(new FileValidationError.NameAboveMaxLength(NAME_MAX_LENGTH));
            }
        }
    }

    public void validateVersion(Integer version) {
        if (version != null && version < VERSION_MIN_VALUE) {
            errors.add(new FileValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
    }

    public void validate() throws FileValidationException {
        if (hasErrors()) {
            throw new FileValidationException(errors);
        }
    }

    public static void validate(File file) throws FileValidationException {
        var validator = new FileValidator();
        validator.validateId(file.id);
        validator.validateUserId(file.userId);
        validator.validateStatus(file.status);
        validator.validateName(file.name);
        validator.validateVersion(file.version);
        validator.validate();
    }
}
