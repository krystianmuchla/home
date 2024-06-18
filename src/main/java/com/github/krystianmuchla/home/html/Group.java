package com.github.krystianmuchla.home.html;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Group {
    private final Stream<Object> content;

    private Group(final Stream<Object> content) {
        this.content = content;
    }

    public static Group group(final Stream<Object> content) {
        return new Group(content);
    }

    public static Group group(final Object... content) {
        return group(Arrays.stream(content));
    }

    @Override
    public String toString() {
        return content.map(Object::toString).collect(Collectors.joining());
    }
}
