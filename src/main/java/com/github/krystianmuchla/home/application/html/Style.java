package com.github.krystianmuchla.home.application.html;

import com.github.krystianmuchla.home.application.util.Resource;

import java.util.stream.Collectors;

public class Style {
    public static final String BACKGROUND = sanitize(Resource.read("ui/style/background.css"));
    public static final String BODY = sanitize(Resource.read("ui/style/body.css"));
    public static final String COLUMN = sanitize(Resource.read("ui/style/column.css"));
    public static final String CONTEXT_MENU = sanitize(Resource.read("ui/style/context-menu.css"));
    public static final String DRIVE = sanitize(Resource.read("ui/style/drive.css"));
    public static final String LABELED_TEXT_INPUT = sanitize(Resource.read("ui/style/labeled-text-input.css"));
    public static final String LEFT_TOP = sanitize(Resource.read("ui/style/left-top.css"));
    public static final String MAIN_BUTTON = sanitize(Resource.read("ui/style/main-button.css"));
    public static final String MODAL = sanitize(Resource.read("ui/style/modal.css"));
    public static final String ON_TOP = sanitize(Resource.read("ui/style/on-top.css"));
    public static final String ROW = sanitize(Resource.read("ui/style/row.css"));
    public static final String SIGN_IN_FORM = sanitize(Resource.read("ui/style/sign-in-form.css"));
    public static final String SIGN_UP_FORM = sanitize(Resource.read("ui/style/sign-up-form.css"));
    public static final String TEXT_INPUT = sanitize(Resource.read("ui/style/text-input.css"));
    public static final String TOAST = sanitize(Resource.read("ui/style/toast.css"));
    public static final String TOASTS_CONTAINER = sanitize(Resource.read("ui/style/toasts-container.css"));

    private static String sanitize(String style) {
        return Sanitizer.removeMultiLineComments(style)
            .lines()
            .map(Sanitizer::removeMultipleWhitespaces)
            .collect(Collectors.joining());
    }
}
