package com.github.krystianmuchla.home.application.html;

import com.github.krystianmuchla.home.application.html.component.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.krystianmuchla.home.application.html.Attribute.*;
import static com.github.krystianmuchla.home.application.html.Group.group;
import static com.github.krystianmuchla.home.application.html.Tag.*;

public class Html {
    public static String document(
        Collection<String> styles,
        Collection<String> scripts,
        Collection<Component> components,
        Object... content
    ) {
        return "<!DOCTYPE html>" + html(styles, scripts, components, content);
    }

    private static Tag html(
        Collection<String> styles,
        Collection<String> scripts,
        Collection<Component> components,
        Object... content
    ) {
        return Tag.html(attrs(lang("en")),
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

    private static String resolveStyle(Collection<String> styles, Collection<Component> elements) {
        var stylesStream = Stream.concat(styles.stream(), elements.stream().flatMap(Component::styles));
        return Style.BODY + String.join("", stylesStream.collect(Collectors.toSet()));
    }

    private static String resolveScript(Collection<String> scripts, Collection<Component> elements) {
        var scriptsStream = Stream.concat(scripts.stream(), elements.stream().flatMap(Component::scripts));
        return String.join("", scriptsStream.collect(Collectors.toSet()));
    }
}
