package com.github.krystianmuchla.home.domain.drive.file.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;

public class IllegalFileStatusException extends DomainException {
    private final FileStatus status;

    public IllegalFileStatusException(FileStatus status) {
        this.status = status;
    }

    public FileStatus getStatus() {
        return status;
    }
}
