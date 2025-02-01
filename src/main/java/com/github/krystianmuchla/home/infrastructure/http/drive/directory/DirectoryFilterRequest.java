package com.github.krystianmuchla.home.infrastructure.http.drive.directory;

import com.github.krystianmuchla.home.application.util.MultiValueMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestQuery;

import java.util.UUID;

public record DirectoryFilterRequest(UUID dir) implements RequestQuery {
    public DirectoryFilterRequest(MultiValueMap<String, String> query) {
        this(resolveDir(query));
    }

    private static UUID resolveDir(MultiValueMap<String, String> query) {
        return query.getFirst("dir").map(UUID::fromString).orElse(null);
    }
}
