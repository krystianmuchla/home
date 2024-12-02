package com.github.krystianmuchla.home.domain.drive.file.exception;

import com.github.krystianmuchla.home.domain.drive.file.FileStatus;

public class IllegalFileStatusException extends Exception {
    private final FileStatus status;

    public IllegalFileStatusException(FileStatus status) {
        this.status = status;
    }

    public FileStatus getStatus() {
        return status;
    }
}
