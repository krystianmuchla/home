package com.github.krystianmuchla.home.html;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tag {
    private final String tag;
    private final Map<String, Object> attributes;
    private final Stream<Object> content;

    private Tag(final String tag, final Map<String, Object> attributes, final Stream<Object> content) {
        this.tag = tag;
        this.attributes = attributes;
        this.content = content;
    }

    public static Tag a(final Map<String, Object> attributes, final Object... content) {
        return new Tag("a", attributes, Arrays.stream(content));
    }

    public static Tag body(final Object... content) {
        return new Tag("body", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag button(final Map<String, Object> attributes, final Object... content) {
        return new Tag("button", attributes, Arrays.stream(content));
    }

    public static Tag div(final Map<String, Object> attributes, final Object... content) {
        return new Tag("div", attributes, Arrays.stream(content));
    }

    public static Tag head(final Object... content) {
        return new Tag("head", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag html(final Object... content) {
        return new Tag("html", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag input(final Map<String, Object> attributes) {
        return new Tag("input", attributes, null);
    }

    public static Tag label(final Map<String, Object> attributes, final Object... content) {
        return new Tag("label", attributes, Arrays.stream(content));
    }

    public static Tag meta(final Map<String, Object> attributes) {
        return new Tag("meta", attributes, null);
    }

    public static Tag script(final Object... content) {
        return new Tag("script", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag style(final Object... content) {
        return new Tag("style", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag title(final Object... content) {
        return new Tag("title", new HashMap<>(), Arrays.stream(content));
    }

    public Tag appendClasses(final String... classes) {
        final var appendage = String.join(" ", classes);
        attributes.merge("class", appendage, (a, b) -> a + " " + b);
        return this;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append("<");
        builder.append(tag);
        if (!attributes.isEmpty()) {
            builder.append(" ");
            final var attributes = this.attributes.entrySet()
                .stream()
                .map(attribute -> attribute.getKey() + "=\"" + attribute.getValue() + "\"")
                .collect(Collectors.joining(" "));
            builder.append(attributes);
        }
        builder.append(">");
        if (content != null) {
            final var content = this.content.map(Object::toString).collect(Collectors.joining());
            builder.append(content);
            builder.append("</");
            builder.append(tag);
            builder.append(">");
        }
        return builder.toString();
    }
}
