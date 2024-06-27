package com.github.krystianmuchla.home.html.component;

import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;

import java.util.stream.Stream;

public class Toast implements Component {
    @Override
    public Stream<String> styles() {
        return Stream.of(Style.COLUMN, Style.TOAST, Style.TOASTS_CONTAINER);
    }

    @Override
    public Stream<String> scripts() {
        return Stream.of(Script.TOAST);
    }
}
