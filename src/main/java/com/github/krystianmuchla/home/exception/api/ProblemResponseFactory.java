package com.github.krystianmuchla.home.exception.api;

import java.util.HashMap;
import java.util.Map;

public class ProblemResponseFactory {
    public static Map<String, Object> create(final Map<String, Object> extensions) {
        return create(null, null, null, null, extensions);
    }

    public static Map<String, Object> create(
        final String type,
        final Integer status,
        final String title,
        final String detail,
        final Map<String, Object> extensions
    ) {
        final var response = new HashMap<String, Object>();
        response.put("type", type);
        response.put("status", status);
        response.put("title", title);
        response.put("detail", detail);
        response.putAll(extensions);
        return response;
    }
}
