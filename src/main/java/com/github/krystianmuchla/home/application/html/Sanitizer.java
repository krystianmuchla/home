package com.github.krystianmuchla.home.application.html;

public class Sanitizer {
    public static String removeMultiLineComments(String text) {
        var builder = new StringBuilder(text.length());
        var remaining = text;
        var open = false;
        while (true) {
            if (open) {
                var index = remaining.indexOf("*/");
                if (index < 0) {
                    break;
                } else {
                    remaining = remaining.substring(index + 2);
                    open = false;
                }
            } else {
                var index = remaining.indexOf("/*");
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

    public static String removeMultipleWhitespaces(String text) {
        return text.replaceAll("\\s{2,}", "");
    }
}
