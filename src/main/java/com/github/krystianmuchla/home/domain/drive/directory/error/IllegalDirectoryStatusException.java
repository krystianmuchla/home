package com.github.krystianmuchla.home.domain.drive.directory.error;

import com.github.krystianmuchla.home.domain.core.error.DomainException;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;

public class IllegalDirectoryStatusException extends DomainException {
    private final DirectoryStatus status;

    public IllegalDirectoryStatusException(DirectoryStatus status) {
        this.status = status;
    }

    public DirectoryStatus getStatus() {
        return status;
    }
}
