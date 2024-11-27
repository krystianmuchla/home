package com.github.krystianmuchla.home.application.html;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.krystianmuchla.home.application.html.Attribute.*;

public class Tag {
    private final String tag;
    public final Map<String, Object> attributes;
    private final Stream<Object> content;

    private Tag(String tag, Map<String, Object> attributes, Stream<Object> content) {
        this.tag = tag;
        this.attributes = attributes;
        this.content = content;
    }

    public static Tag a(Map<String, Object> attributes, Object... content) {
        return new Tag("a", attributes, Arrays.stream(content));
    }

    public static Tag body(Object... content) {
        return new Tag("body", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag button(Map<String, Object> attributes, Object... content) {
        return new Tag("button", attributes, Arrays.stream(content));
    }

    public static Tag div(Map<String, Object> attributes, Object... content) {
        return new Tag("div", attributes, Arrays.stream(content));
    }

    public static Tag head(Object... content) {
        return new Tag("head", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag html(Map<String, Object> attributes, Object... content) {
        return new Tag("html", attributes, Arrays.stream(content));
    }

    public static Tag img(Map<String, Object> attributes) {
        return new Tag("img", attributes, null);
    }

    public static Tag input(Map<String, Object> attributes) {
        return new Tag("input", attributes, null);
    }

    public static Tag label(Map<String, Object> attributes, Object... content) {
        return new Tag("label", attributes, Arrays.stream(content));
    }

    public static Tag labeledTextInput(String label, String id, String type) {
        return div(attrs(clazz("labeled-text-input")),
            label(attrs(fur(id)),
                label
            ),
            input(attrs(id(id), clazz("text-input"), type(type)))
        );
    }

    public static Tag link(Map<String, Object> attributes) {
        return new Tag("link", attributes, null);
    }

    public static Tag meta(Map<String, Object> attributes) {
        return new Tag("meta", attributes, null);
    }

    public static Tag script(Map<String, Object> attributes, Object... content) {
        return new Tag("script", attributes, Arrays.stream(content));
    }

    public static Tag span(Map<String, Object> attributes, Object... content) {
        return new Tag("span", attributes, Arrays.stream(content));
    }

    public static Tag style(Object... content) {
        return new Tag("style", new HashMap<>(), Arrays.stream(content));
    }

    public static Tag title(Object... content) {
        return new Tag("title", new HashMap<>(), Arrays.stream(content));
    }

    public Tag appendClass(String... classes) {
        var appendage = String.join(" ", classes);
        attributes.merge("class", appendage, (a, b) -> a + " " + b);
        return this;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("<");
        builder.append(tag);
        if (!attributes.isEmpty()) {
            builder.append(" ");
            var attributes = this.attributes.entrySet()
                .stream()
                .map(attribute -> attribute.getKey() + "=\"" + attribute.getValue() + "\"")
                .collect(Collectors.joining(" "));
            builder.append(attributes);
        }
        builder.append(">");
        if (content != null) {
            var content = this.content.map(Object::toString).collect(Collectors.joining());
            builder.append(content);
            builder.append("</");
            builder.append(tag);
            builder.append(">");
        }
        return builder.toString();
    }
}
