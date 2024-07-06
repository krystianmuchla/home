package com.github.krystianmuchla.home.html.component;

import java.util.stream.Stream;

public interface Component {
    Component HTTP = new Http();
    Component LABELED_TEXT_INPUT = new LabeledTextInput();
    Component TOAST = new Toast();

    Stream<String> styles();

    Stream<String> scripts();
}
