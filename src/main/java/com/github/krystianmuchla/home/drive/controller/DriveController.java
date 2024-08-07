package com.github.krystianmuchla.home.drive.controller;

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
    public static final String PATH = "/drive";

    public DriveController() {
        super(PATH);
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        ResponseWriter.writeHtml(exchange, 200, html());
    }

    private String html() {
        return document(
            Set.of(
                Style.BACKGROUND,
                Style.COLUMN,
                Style.DRIVE,
                Style.LEFT_TOP,
                Style.MAIN_BUTTON,
                Style.ROW
            ),
            Set.of(Script.DRIVE),
            Set.of(Component.CONTEXT_MENU, Component.TOAST),
            div(attrs(clazz("background"), style("grid-template-rows: auto 1fr;")),
                div(attrs(clazz("left-top row"), style("padding: 10px; gap: 10px;")),
                    div(attrs(id("upload-file"), clazz("main-button")),
                        "Upload file"
                    ),
                    div(attrs(id("create-dir"), clazz("main-button")),
                        "Create directory"
                    )
                ),
                div(attrs(id("main"), clazz("left-top")))
            )
        );
    }
}
