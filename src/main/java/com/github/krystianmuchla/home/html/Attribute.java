package com.github.krystianmuchla.home.html;

import java.util.HashMap;
import java.util.Map;

public class Attribute {
    public static Map<String, Object> attrs(final String... keysAndValues) {
        final var attributes = new HashMap<String, Object>();
        String key = null;
        for (int index = 0; index < keysAndValues.length; index++) {
            if (index % 2 == 0) {
                key = keysAndValues[index];
            } else {
                attributes.put(key, keysAndValues[index]);
            }
        }
        return attributes;
    }
}
