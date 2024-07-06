package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.github.krystianmuchla.home.html.Attribute.attrs;
import static com.github.krystianmuchla.home.html.Attribute.clazz;
import static com.github.krystianmuchla.home.html.Group.group;
import static com.github.krystianmuchla.home.html.Tag.div;

public class DriveUiController extends Controller {
    public DriveUiController() {
        super("/ui/drive");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var list = DriveService.listDirectory(user.id(), request.dir());
        ResponseWriter.writeHtml(exchange, 200, html(list));
    }

    private Object html(final List<File> list) {
        return group(list.stream().map(row -> {
            final var clazz = row.isDirectory() ? "dir" : "file";
            return div(attrs(clazz(clazz)),
                row.getName()
            );
        }));
    }
}
