package com.github.krystianmuchla.home.ui.element;

import j2html.tags.DomContent;

import static j2html.TagCreator.*;

public class LabeledTextInput {
    public static DomContent html(final String label, final String id, final String type) {
        return div(attrs(".labeled-text-input"),
            label(label).withFor(id),
            input(attrs(".text-input")).withId(id).withType(type)
        );
    }
}
