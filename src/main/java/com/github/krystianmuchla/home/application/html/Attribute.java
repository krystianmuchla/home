package com.github.krystianmuchla.home.application.html;

import java.util.HashMap;
import java.util.Map;

public class Attribute {
    public static Map<String, Object> attrs(Map.Entry<?, ?>... attrs) {
        var result = new HashMap<String, Object>();
        for (var attr : attrs) {
            result.put((String) attr.getKey(), attr.getValue());
        }
        return result;
    }

    public static Map.Entry<String, Object> clazz(Object clazz) {
        return Map.entry("class", clazz);
    }

    public static Map.Entry<String, Object> content(Object content) {
        return Map.entry("content", content);
    }

    public static Map.Entry<String, Object> fur(Object fur) {
        return Map.entry("for", fur);
    }

    public static Map.Entry<String, Object> href(Object href) {
        return Map.entry("href", href);
    }

    public static Map.Entry<String, Object> id(Object id) {
        return Map.entry("id", id);
    }

    public static Map.Entry<String, Object> lang(Object lang) {
        return Map.entry("lang", lang);
    }

    public static Map.Entry<String, Object> name(Object name) {
        return Map.entry("name", name);
    }

    public static Map.Entry<String, Object> style(Object style) {
        return Map.entry("style", style);
    }

    public static Map.Entry<String, Object> type(Object type) {
        return Map.entry("type", type);
    }
}
