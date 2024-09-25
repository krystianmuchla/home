package com.github.krystianmuchla.home.application.html;

import com.github.krystianmuchla.home.application.util.Resource;

import java.util.stream.Collectors;

public class Image {
    public static final String CONTEXT_MENU = sanitize(Resource.read("ui/image/context-menu.svg"));

    private static String sanitize(String image) {
        return image.lines().map(Sanitizer::removeMultipleWhitespaces).collect(Collectors.joining());
    }
}
