package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestQuery;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.BadRequestException;

import java.util.UUID;

import static com.github.krystianmuchla.home.domain.drive.DriveValidator.validateDirectoryId;
import static com.github.krystianmuchla.home.domain.drive.DriveValidator.validateFileId;

public record DriveFilterRequest(UUID dir, UUID file) implements RequestQuery {
    public DriveFilterRequest(MultiValueMap<String, String> query) {
        this(resolveDir(query), resolveFile(query));
    }

    private static UUID resolveDir(MultiValueMap<String, String> query) {
        return query.getFirst("dir").map(dir -> {
            var errors = validateDirectoryId(dir);
            if (!errors.isEmpty()) {
                throw new BadRequestException("dir", errors);
            }
            return UUID.fromString(dir);
        }).orElse(null);
    }

    private static UUID resolveFile(MultiValueMap<String, String> query) {
        return query.getFirst("file").map(file -> {
            var errors = validateFileId(file);
            if (!errors.isEmpty()) {
                throw new BadRequestException("file", errors);
            }
            return UUID.fromString(file);
        }).orElse(null);
    }
}
