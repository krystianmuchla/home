package com.github.krystianmuchla.home.application.html;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tags {
    private final Stream<Tag> tags;

    private Tags(Stream<Tag> tags) {
        this.tags = tags;
    }

    public static Tags tags(Stream<Tag> content) {
        return new Tags(content);
    }

    public static Tags tags(Tag... content) {
        return tags(Arrays.stream(content));
    }

    @Override
    public String toString() {
        return tags.map(Tag::toString).collect(Collectors.joining());
    }
}
