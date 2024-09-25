package com.github.krystianmuchla.home.infrastructure.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookie {
    public static String create(String key, String value) {
        return key + "=" + value + "; Path=/; SameSite=Strict; HttpOnly";
    }

    public static Map<String, String> parse(String cookie) {
        if (cookie == null) {
            return Map.of();
        }
        var cookies = new HashMap<String, String>();
        var entries = Arrays.stream(cookie.split(";"))
            .map(String::trim)
            .map(keyAndValue -> {
                var separatorIndex = keyAndValue.indexOf("=");
                if (separatorIndex < 0) {
                    return null;
                }
                var key = keyAndValue.substring(0, separatorIndex);
                var value = keyAndValue.substring(separatorIndex + 1);
                return Map.entry(key, value);
            })
            .filter(Objects::nonNull)
            .toList();
        for (int index = entries.size() - 1; index >= 0; index--) {
            var entry = entries.get(index);
            cookies.put(entry.getKey(), entry.getValue());
        }
        return cookies;
    }
}
