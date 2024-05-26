package com.github.krystianmuchla.home.html.element;

import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.Tag;

import java.util.Set;

import static com.github.krystianmuchla.home.html.Attribute.attributes;
import static com.github.krystianmuchla.home.html.Tag.div;

public record Listing(Object... elements) implements Element {
    @Override
    public Set<String> styles() {
        return Set.of(Style.LIST);
    }

    @Override
    public Set<String> scripts() {
        return Set.of();
    }

    @Override
    public Tag tag() {
        return div(attributes("class", "list"),
            elements
        );
    }
}
