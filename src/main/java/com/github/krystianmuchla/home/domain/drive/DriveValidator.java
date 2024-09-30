package com.github.krystianmuchla.home.domain.drive;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.exception.Validator;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;

import java.util.List;
import java.util.UUID;

public class DriveValidator extends Validator {
    private static final int ENTRY_NAME_MAX_LENGTH = 255;

    public static List<ValidationError> validateDirectoryId(String directoryId) {
        return validateUuid(directoryId);
    }

    public static List<ValidationError> validateDirectoryId(UUID directoryId) {
        return validateUuid(directoryId);
    }

    public static List<ValidationError> validateDirectoryStatus(DirectoryStatus directoryStatus) {
        if (directoryStatus == null) {
            return List.of(ValidationError.nullValue());
        }
        return List.of();
    }

    public static List<ValidationError> validateDirectoryName(String directoryName) {
        return validateEntryName(directoryName);
    }

    public static List<ValidationError> validateFileId(String fileId) {
        return validateUuid(fileId);
    }

    public static List<ValidationError> validateFileId(UUID fileId) {
        return validateUuid(fileId);
    }

    public static List<ValidationError> validateFileStatus(FileStatus fileStatus) {
        if (fileStatus == null) {
            return List.of(ValidationError.nullValue());
        }
        return List.of();
    }

    public static List<ValidationError> validateFileName(String fileName) {
        return validateEntryName(fileName);
    }

    private static List<ValidationError> validateEntryName(String entryName) {
        if (entryName == null) {
            return List.of(ValidationError.nullValue());
        }
        if (entryName.isBlank()) {
            return List.of(ValidationError.emptyValue());
        }
        if (entryName.length() > ENTRY_NAME_MAX_LENGTH) {
            return List.of(ValidationError.aboveMaxLength(ENTRY_NAME_MAX_LENGTH));
        }
        return List.of();
    }
}
