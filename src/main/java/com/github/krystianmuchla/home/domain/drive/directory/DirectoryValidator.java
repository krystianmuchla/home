package com.github.krystianmuchla.home.domain.drive.directory;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationError;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;

import java.util.UUID;

public class DirectoryValidator extends Validator<DirectoryValidationError, DirectoryValidationException> {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int VERSION_MIN_VALUE = 1;

    private final Directory directory;

    public DirectoryValidator(Directory directory) {
        this.directory = directory;
    }

    public DirectoryValidator checkId() {
        if (directory.id == null) {
            errors.add(new DirectoryValidationError.NullId());
        }
        return this;
    }

    public DirectoryValidator checkUserId() {
        if (directory.userId == null) {
            errors.add(new DirectoryValidationError.NullUserId());
        }
        return this;
    }

    public DirectoryValidator checkStatus() {
        return checkStatus(directory.status);
    }

    public DirectoryValidator checkStatus(DirectoryStatus status) {
        if (status == null) {
            errors.add(new DirectoryValidationError.NullStatus());
        }
        return this;
    }

    public DirectoryValidator checkParentId() {
        return checkParentId(directory.parentId);
    }

    public DirectoryValidator checkParentId(UUID parentId) {
        if (parentId != null && parentId.equals(directory.id)) {
            errors.add(new DirectoryValidationError.InvalidHierarchy());
        }
        return this;
    }

    public DirectoryValidator checkName() {
        return checkName(directory.name);
    }

    public DirectoryValidator checkName(String name) {
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
        return this;
    }

    public DirectoryValidator checkCreationTime() {
        if (directory.creationTime == null) {
            errors.add(new DirectoryValidationError.NullCreationTime());
        }
        return this;
    }

    public DirectoryValidator checkModificationTime() {
        return checkModificationTime(directory.modificationTime);
    }

    public DirectoryValidator checkModificationTime(Time modificationTime) {
        if (modificationTime == null) {
            errors.add(new DirectoryValidationError.NullModificationTime());
        }
        return this;
    }

    public DirectoryValidator checkVersion() {
        return checkVersion(directory.version);
    }

    public DirectoryValidator checkVersion(Integer version) {
        if (version == null) {
            errors.add(new DirectoryValidationError.NullVersion());
        } else if (version < VERSION_MIN_VALUE) {
            errors.add(new DirectoryValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    public void validate() throws DirectoryValidationException {
        if (hasErrors()) {
            throw new DirectoryValidationException(errors);
        }
    }

    public static void validate(Directory directory) throws DirectoryValidationException {
        new DirectoryValidator(directory)
            .checkId()
            .checkUserId()
            .checkStatus()
            .checkParentId()
            .checkName()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
