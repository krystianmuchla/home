package com.github.krystianmuchla.home.html.component;

import com.github.krystianmuchla.home.html.Script;

import java.util.stream.Stream;

public class Http implements Component {
    @Override
    public Stream<String> styles() {
        return Stream.of();
    }

    @Override
    public Stream<String> scripts() {
        return Stream.of(Script.HTTP, Script.TOAST);
    }
}
