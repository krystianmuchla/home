package com.github.krystianmuchla.home.html.element;

import com.github.krystianmuchla.home.html.Tag;

import java.util.Set;

public interface Element {
    Set<String> styles();
    Set<String> scripts();
    Tag tag();
}
