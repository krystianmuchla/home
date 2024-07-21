package com.github.krystianmuchla.home.html.component;

import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;

import java.util.stream.Stream;

public class ContextMenu implements Component {
    @Override
    public Stream<String> styles() {
        return Stream.of(Style.BACKGROUND, Style.COLUMN, Style.CONTEXT_MENU, Style.ON_TOP);
    }

    @Override
    public Stream<String> scripts() {
        return Stream.of(Script.CONTEXT_MENU);
    }
}
