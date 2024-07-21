package com.github.krystianmuchla.home.html;

import com.github.krystianmuchla.home.util.Resource;

import java.util.stream.Collectors;

public class Image {
    public static final String CONTEXT_MENU = sanitize(Resource.read("ui/image/context-menu.svg"));

    private static String sanitize(final String image) {
        return image.lines().map(Image::removeMultipleWhitespaces).collect(Collectors.joining());
    }

    private static String removeMultipleWhitespaces(final String image) {
        return image.replaceAll("\\s{2,}", "");
    }
}
