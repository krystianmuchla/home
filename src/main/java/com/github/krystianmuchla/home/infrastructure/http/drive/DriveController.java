package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static com.github.krystianmuchla.home.application.util.Resource.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.style;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.*;

public class DriveController extends Controller {
    public static final DriveController INSTANCE = new DriveController();

    public DriveController() {
        super("/drive");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        ResponseWriter.writeHtml(exchange, 200, response());
    }

    private String response() {
        return "<!DOCTYPE html>" + html(attrs(lang("en")),
            head(
                title("Home"),
                meta(attrs(name("viewport"), content("width=device-width, initial-scale=1.0"))),
                link(attrs(rel("stylesheet"), href(COMMON_STYLE.urlPath))),
                link(attrs(rel("stylesheet"), href(DRIVE_STYLE.urlPath))),
                script(attrs(type("module"), src(DRIVE_SCRIPT.urlPath), defer()))
            ),
            body(
                div(attrs(clazz("background"), style("grid-template-rows: auto 1fr;")),
                    div(attrs(clazz("left-top row"), style("padding: 10px; gap: 10px;")),
                        div(attrs(id("upload-file"), clazz("main-button")),
                            "Upload file"
                        ),
                        div(attrs(id("create-dir"), clazz("main-button")),
                            "Create directory"
                        )
                    ),
                    div(attrs(id("router"), clazz("left-top column")))
                )
            )
        );
    }
}
