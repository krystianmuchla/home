package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.component.Component;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.div;

public class DriveController extends Controller {
    public DriveController() {
        super("/drive");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        session(exchange);
        ResponseWriter.writeHtml(exchange, 200, html());
    }

    private String html() {
        return document(
            Set.of(
                Style.BACKGROUND,
                Style.COLUMN,
                Style.LEFT_TOP,
                Style.MAIN_BUTTON,
                Style.ROW
            ),
            Set.of(Script.DRIVE),
            Set.of(Component.TOAST),
            div(attrs(clazz("background"), style("grid-template-rows: auto 1fr;")),
                div(attrs(clazz("left-top row"), style("padding: 10px; gap: 10px;")),
                    div(attrs(id("upload-file"), clazz("main-button")),
                        "Upload file"
                    ),
                    div(attrs(id("create-dir"), clazz("main-button")),
                        "Create directory"
                    )
                ),
                div(attrs(id("ls-container"), clazz("left-top column"), style("padding: 0px 10px;")))
            )
        );
    }
}
