package com.github.krystianmuchla.home.html;

import com.github.krystianmuchla.home.html.component.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Group.group;
import static com.github.krystianmuchla.home.html.Tag.*;

public class Html {
    public static String document(
        final Collection<String> styles,
        final Collection<String> scripts,
        final Collection<Component> components,
        final Object... content
    ) {
        return "<!DOCTYPE html>" + html(styles, scripts, components, content);
    }

    private static Tag html(
        final Collection<String> styles,
        final Collection<String> scripts,
        final Collection<Component> components,
        final Object... content
    ) {
        return Tag.html(
            head(
                title("Home"),
                meta(attrs(name("viewport"), content("width=device-width, initial-scale=1.0"))),
                Tag.style(resolveStyle(styles, components))
            ),
            body(
                group(content),
                script(resolveScript(scripts, components))
            )
        );
    }

    private static String resolveStyle(final Collection<String> styles, final Collection<Component> elements) {
        final var stylesStream = Stream.concat(styles.stream(), elements.stream().flatMap(Component::styles));
        return Style.BODY + String.join("", stylesStream.collect(Collectors.toSet()));
    }

    private static String resolveScript(final Collection<String> scripts, final Collection<Component> elements) {
        final var scriptsStream = Stream.concat(scripts.stream(), elements.stream().flatMap(Component::scripts));
        return String.join("", scriptsStream.collect(Collectors.toSet()));
    }
}
