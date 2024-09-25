package com.github.krystianmuchla.home.domain.drive.api;

import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestQuery;

import java.util.UUID;

public record DriveFilterRequest(UUID dir, UUID file) implements RequestQuery {
    public DriveFilterRequest(MultiValueMap<String, String> query) {
        this(resolveDir(query), resolveFile(query));
    }

    private static UUID resolveDir(MultiValueMap<String, String> query) {
        return query.getFirst("dir").map(UUID::fromString).orElse(null);
    }

    private static UUID resolveFile(MultiValueMap<String, String> query) {
        return query.getFirst("file").map(UUID::fromString).orElse(null);
    }
}
