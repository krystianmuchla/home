package com.github.krystianmuchla.home.html.element;

import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.Tag;

import java.util.Set;

import static com.github.krystianmuchla.home.html.Attribute.attrs;
import static com.github.krystianmuchla.home.html.Tag.*;

public record LabeledTextInput(String labelText, String inputId, String inputType) implements Element {
    @Override
    public Set<String> styles() {
        return Set.of(Style.LABELED_TEXT_INPUT, Style.TEXT_INPUT);
    }

    @Override
    public Set<String> scripts() {
        return Set.of();
    }

    @Override
    public Tag tag() {
        return div(attrs("class", "labeled-text-input"),
            label(attrs("for", inputId),
                labelText
            ),
            input(attrs("id", inputId, "class", "text-input", "type", inputType))
        );
    }
}
