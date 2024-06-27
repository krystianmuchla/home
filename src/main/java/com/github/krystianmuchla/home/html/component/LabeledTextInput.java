package com.github.krystianmuchla.home.html.component;

import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.Tag;

import java.util.stream.Stream;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Tag.*;

public class LabeledTextInput implements Component {
    @Override
    public Stream<String> styles() {
        return Stream.of(Style.LABELED_TEXT_INPUT, Style.TEXT_INPUT);
    }

    @Override
    public Stream<String> scripts() {
        return Stream.empty();
    }

    public static Tag labeledTextInput(final String label, final String id, final String type) {
        return div(attrs(clazz("labeled-text-input")),
            label(attrs(fur(id)),
                label
            ),
            input(attrs(id(id), clazz("text-input"), type(type)))
        );
    }
}
