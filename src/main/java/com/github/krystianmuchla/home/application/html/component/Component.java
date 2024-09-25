package com.github.krystianmuchla.home.application.html.component;

import java.util.stream.Stream;

public interface Component {
    Component LABELED_TEXT_INPUT = new LabeledTextInput();
    Component CONTEXT_MENU = new ContextMenu();
    Component TOAST = new Toast();

    Stream<String> styles();

    Stream<String> scripts();
}
