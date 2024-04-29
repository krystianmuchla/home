package com.github.krystianmuchla.home.ui;

import j2html.tags.DomContent;

import java.util.List;

import static j2html.TagCreator.*;

public class Html {
    public static String dom(final List<String> styles, final List<String> scripts, final DomContent child) {
        return dom(styles, scripts, List.of(child));
    }

    public static String dom(final List<String> styles, List<String> scripts, List<DomContent> children) {
        return document(
            html(
                head(
                    title("Home"),
                    style(Style.BODY + String.join("", styles))
                ),
                body(
                    each(children, child -> child),
                    script(String.join("", scripts))
                )
            )
        );
    }
}
