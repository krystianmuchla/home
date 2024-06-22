package com.github.krystianmuchla.home.html;

import java.util.HashMap;
import java.util.Map;

public class Attribute {
    public static Map<String, Object> attrs(final Map.Entry<?, ?>... attrs) {
        final var result = new HashMap<String, Object>();
        for (final var attr : attrs) {
            result.put((String) attr.getKey(), attr.getValue());
        }
        return result;
    }

    public static Map.Entry<String, Object> clazz(final Object clazz) {
        return Map.entry("class", clazz);
    }

    public static Map.Entry<String, Object> content(final Object content) {
        return Map.entry("content", content);
    }

    public static Map.Entry<String, Object> fur(final Object fur) {
        return Map.entry("for", fur);
    }

    public static Map.Entry<String, Object> href(final Object href) {
        return Map.entry("href", href);
    }

    public static Map.Entry<String, Object> id(final Object id) {
        return Map.entry("id", id);
    }

    public static Map.Entry<String, Object> name(final Object name) {
        return Map.entry("name", name);
    }

    public static Map.Entry<String, Object> style(final Object style) {
        return Map.entry("style", style);
    }

    public static Map.Entry<String, Object> type(final Object type) {
        return Map.entry("type", type);
    }
}
