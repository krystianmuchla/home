package com.github.krystianmuchla.home.infrastructure.http.drive.file;

import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestQuery;

import java.util.UUID;

public record FileFilterRequest(UUID file) implements RequestQuery {
    public FileFilterRequest(MultiValueMap<String, String> query) {
        this(resolveFile(query));
    }

    private static UUID resolveFile(MultiValueMap<String, String> query) {
        return query.getFirst("file").map(UUID::fromString).orElse(null);
    }
}
