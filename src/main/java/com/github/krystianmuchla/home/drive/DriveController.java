package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.html.Script;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.component.Component;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.user.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Group.group;
import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.div;

public class DriveController extends Controller {
    public DriveController() {
        super("/drive");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final User user = session(exchange).user();
        final var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var list = DriveService.listDirectory(user.id(), filter.dir());
        ResponseWriter.writeHtml(exchange, 200, html(list));
    }

    private String html(final List<File> list) {
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
                div(attrs(clazz("left-top column"), style("padding: 0px 10px;")),
                    group(list.stream().map(row -> {
                        final var clazz = row.isDirectory() ? "dir" : "file";
                        return div(attrs(clazz(clazz)),
                            row.getName()
                        );
                    }))
                )
            )
        );
    }
}
