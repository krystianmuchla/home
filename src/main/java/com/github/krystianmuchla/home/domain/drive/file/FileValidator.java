package com.github.krystianmuchla.home.domain.drive.file;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.core.error.Validator;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationError;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;

import java.util.UUID;

public class FileValidator extends Validator<FileValidationError, FileValidationException> {
    private static final int NAME_MAX_LENGTH = 255;
    private static final int VERSION_MIN_VALUE = 1;

    private final File file;

    public FileValidator(File file) {
        this.file = file;
    }

    public FileValidator checkId() {
        if (file.id == null) {
            errors.add(new FileValidationError.NullId());
        }
        return this;
    }

    public FileValidator checkUserId() {
        if (file.userId == null) {
            errors.add(new FileValidationError.NullUserId());
        }
        return this;
    }

    public FileValidator checkStatus() {
        return checkStatus(file.status);
    }

    public FileValidator checkStatus(FileStatus status) {
        if (status == null) {
            errors.add(new FileValidationError.NullStatus());
        }
        return this;
    }

    public FileValidator checkDirectoryId() {
        return checkDirectoryId(file.directoryId);
    }

    public FileValidator checkDirectoryId(UUID directoryId) {
        // noop
        return this;
    }

    public FileValidator checkName() {
        return checkName(file.name);
    }

    public FileValidator checkName(String name) {
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
        return this;
    }

    public FileValidator checkCreationTime() {
        if (file.creationTime == null) {
            errors.add(new FileValidationError.NullCreationTime());
        }
        return this;
    }

    public FileValidator checkModificationTime() {
        return checkModificationTime(file.modificationTime);
    }

    public FileValidator checkModificationTime(Time modificationTime) {
        if (modificationTime == null) {
            errors.add(new FileValidationError.NullModificationTime());
        }
        return this;
    }

    public FileValidator checkVersion() {
        return checkVersion(file.version);
    }

    public FileValidator checkVersion(Integer version) {
        if (version == null) {
            errors.add(new FileValidationError.NullVersion());
        } else if (version < VERSION_MIN_VALUE) {
            errors.add(new FileValidationError.VersionBelowMinValue(VERSION_MIN_VALUE));
        }
        return this;
    }

    public void validate() throws FileValidationException {
        if (hasErrors()) {
            throw new FileValidationException(errors);
        }
    }

    public static void validate(File file) throws FileValidationException {
        new FileValidator(file)
            .checkId()
            .checkUserId()
            .checkStatus()
            .checkDirectoryId()
            .checkName()
            .checkCreationTime()
            .checkModificationTime()
            .checkVersion()
            .validate();
    }
}
