package com.github.krystianmuchla.home.application.util;

import java.util.HashSet;
import java.util.Set;

public class StringService {
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static Set<Character> toSet(String string) {
        var set = new HashSet<Character>(string.length());
        for (var character : string.toCharArray()) {
            set.add(character);
        }
        return set;
    }
}
