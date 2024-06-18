package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public record DriveFilterRequest(List<String> dir, String file) implements RequestQuery {
    public DriveFilterRequest(final MultiValueMap<String, String> query) {
        this(resolveDir(query), resolveFile(query));
    }

    private static List<String> resolveDir(final MultiValueMap<String, String> query) {
        final var dir = query.get("dir");
        if (dir == null) {
            return new ArrayList<>();
        }
        return dir;
    }

    private static String resolveFile(final MultiValueMap<String, String> query) {
        return query.getFirst("file").orElse(null);
    }
}
