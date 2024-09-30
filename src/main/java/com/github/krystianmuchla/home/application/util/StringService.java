package com.github.krystianmuchla.home.application.util;

public class StringService {
    public static boolean containsAny(String string, char[] chars) {
        for (var character : chars) {
            if (contains(string, character)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsOnly(String string, char[] chars) {
        var charsString = String.valueOf(chars);
        for (char character : string.toCharArray()) {
            if (!contains(charsString, character)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(String string, char character) {
        return string.indexOf(character) >= 0;
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
