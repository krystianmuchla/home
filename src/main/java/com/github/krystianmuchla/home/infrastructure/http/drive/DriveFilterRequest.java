package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestQuery;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.BadRequestException;

import java.util.UUID;

public record DriveFilterRequest(UUID dir, UUID file) implements RequestQuery {
    public DriveFilterRequest(MultiValueMap<String, String> query) {
        this(resolveDir(query), resolveFile(query));
    }

    private static UUID resolveDir(MultiValueMap<String, String> query) {
        return query.getFirst("dir").map(dir -> {
            try {
                return UUID.fromString(dir);
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException("dir", ValidationError.wrongFormat());
            }
        }).orElse(null);
    }

    private static UUID resolveFile(MultiValueMap<String, String> query) {
        return query.getFirst("file").map(file -> {
            try {
                return UUID.fromString(file);
            } catch (IllegalArgumentException exception) {
                throw new BadRequestException("file", ValidationError.wrongFormat());
            }
        }).orElse(null);
    }
}
