package com.github.krystianmuchla.home.html;

import com.github.krystianmuchla.home.util.Resource;

import java.util.stream.Collectors;

public class Script {
    public static final String CONTEXT_MENU = sanitize(Resource.read("ui/script/context-menu.js"));
    public static final String DRIVE = sanitize(Resource.read("ui/script/drive.js"));
    public static final String SIGN_IN_FORM = sanitize(Resource.read("ui/script/sign-in-form.js"));
    public static final String SIGN_UP_FORM = sanitize(Resource.read("ui/script/sign-up-form.js"));
    public static final String TOAST = sanitize(Resource.read("ui/script/toast.js"));

    private static String sanitize(String script) {
        return Sanitizer.removeMultiLineComments(script)
            .lines()
            .map(Script::removeOneLineComment)
            .map(Sanitizer::removeMultipleWhitespaces)
            .collect(Collectors.joining());
    }

    private static String removeOneLineComment(String script) {
        var index = script.indexOf("//");
        if (index > 0) {
            return script.substring(0, index);
        }
        return script;
    }
}
