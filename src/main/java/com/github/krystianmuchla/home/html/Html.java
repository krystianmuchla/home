package com.github.krystianmuchla.home.html;

import com.github.krystianmuchla.home.html.element.Element;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.github.krystianmuchla.home.html.Attribute.attributes;
import static com.github.krystianmuchla.home.html.Group.group;
import static com.github.krystianmuchla.home.html.Tag.*;

public class Html {
    public static String document(
        final Collection<String> styles,
        final Collection<String> scripts,
        final Collection<Element> elements,
        final Object... content
    ) {
        return "<!DOCTYPE html>" + html(styles, scripts, elements, content);
    }

    private static String html(
        final Collection<String> styles,
        final Collection<String> scripts,
        final Collection<Element> elements,
        final Object... content
    ) {
        return Tag.html(
            head(
                title("Home"),
                meta(attributes("name", "viewport", "content", "width=device-width, initial-scale=1.0")),
                style(resolveStyle(styles, elements))
            ),
            body(
                group(content),
                script(resolveScript(scripts, elements))
            )
        ).toString();
    }

    private static String resolveStyle(final Collection<String> styles, final Collection<Element> elements) {
        final var elementStyles = elements.stream()
            .map(Element::styles)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        return Style.BODY + String.join("", styles) + String.join("", elementStyles);
    }

    private static String resolveScript(final Collection<String> scripts, final Collection<Element> elements) {
        final var elementScripts = elements.stream()
            .map(Element::scripts)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
        return String.join("", scripts) + String.join("", elementScripts);
    }
}
