package com.github.krystianmuchla.home.html;

import com.github.krystianmuchla.home.util.Resource;

import java.util.stream.Collectors;

public class Script {
    public static final String DRIVE = sanitize(Resource.read("ui/script/drive.js"));
    public static final String SIGN_IN_FORM = sanitize(Resource.read("ui/script/sign-in-form.js"));
    public static final String SIGN_UP_FORM = sanitize(Resource.read("ui/script/sign-up-form.js"));

    private static String sanitize(final String script) {
        return removeMultiLineComments(script)
            .lines()
            .map(Script::removeOneLineComment)
            .map(Script::removeMultipleWhitespaces)
            .collect(Collectors.joining());
    }

    public static String removeMultiLineComments(final String script) {
        final var builder = new StringBuilder(script.length());
        var remaining = script;
        var open = false;
        while (true) {
            if (open) {
                final var index = remaining.indexOf("*/");
                if (index < 0) {
                    break;
                }
                else {
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

    private static String removeOneLineComment(final String scriptLine) {
        final var index = scriptLine.indexOf("//");
        if (index > 0) {
            return scriptLine.substring(0, index);
        }
        return scriptLine;
    }

    private static String removeMultipleWhitespaces(final String script) {
        return script.replaceAll("\\s{2,}", "");
    }
}
