package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.util.MultiValueMap;

public record DriveFilterRequest(String path) implements RequestQuery {
    public DriveFilterRequest(final MultiValueMap<String, String> query) {
        this(resolvePath(query));
    }

    private static String resolvePath(final MultiValueMap<String, String> query) {
        return query.getFirst("path").orElse(null);
    }
}
