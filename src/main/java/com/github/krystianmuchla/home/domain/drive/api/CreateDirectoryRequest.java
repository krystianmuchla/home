package com.github.krystianmuchla.home.domain.drive.api;

import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

import java.util.UUID;

import static com.github.krystianmuchla.home.domain.drive.DriveValidator.validateDirectoryName;

public record CreateDirectoryRequest(UUID dir, String name) implements RequestBody {
    @Override
    public void validate() {
        var errors = validateDirectoryName(name);
        if (!errors.isEmpty()) {
            throw new BadRequestException("name", errors);
        }
    }
}
