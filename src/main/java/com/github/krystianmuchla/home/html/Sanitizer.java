package com.github.krystianmuchla.home.html;

public class Sanitizer {
    public static String removeMultiLineComments(final String text) {
        final var builder = new StringBuilder(text.length());
        var remaining = text;
        var open = false;
        while (true) {
            if (open) {
                final var index = remaining.indexOf("*/");
                if (index < 0) {
                    break;
                } else {
                    remaining = remaining.substring(index + 2);
                    open = false;
                }
            } else {
                final var index = remaining.indexOf("/*");
                if (index < 0) {
                    builder.append(remaining);
                    break;
                } else {
                    builder.append(remaining, 0, index);
                    remaining = remaining.substring(index + 2);
                    open = true;
                }
            }
        }
        return builder.toString();
    }

    public static String removeMultipleWhitespaces(final String text) {
        return text.replaceAll("\\s{2,}", "");
    }
}
