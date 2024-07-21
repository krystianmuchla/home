package com.github.krystianmuchla.home.drive.controller;

import com.github.krystianmuchla.home.drive.DriveService;
import com.github.krystianmuchla.home.drive.Entry;
import com.github.krystianmuchla.home.drive.api.DriveFilterRequest;
import com.github.krystianmuchla.home.drive.directory.Directory;
import com.github.krystianmuchla.home.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.html.Group;
import com.github.krystianmuchla.home.html.Tag;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.util.CollectionService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.krystianmuchla.home.html.Attribute.*;
import static com.github.krystianmuchla.home.html.Group.group;
import static com.github.krystianmuchla.home.html.Tag.div;
import static com.github.krystianmuchla.home.html.Tag.span;

public class DriveUiController extends Controller {
    public DriveUiController() {
        super("/ui/drive/main");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final var user = RequestReader.readUser(exchange);
        final var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var path = DirectoryService.getPath(user.id(), request.dir());
        final var list = DriveService.listDirectory(user.id(), request.dir());
        ResponseWriter.writeHtml(exchange, 200, html(user.name(), path, list));
    }

    private Group html(final String userName, final List<Directory> path, final List<Entry> list) {
        return group(
            div(attrs(id("path")), path(userName, path)),
            div(attrs(id("list"), clazz("column")),
                group(
                    list.stream().map(
                        entry -> div(attrs(id(entry.id()), clazz(entry.type().asClass())),
                            entry.name()
                        )
                    )
                )
            )
        );
    }

    private String path(final String userName, final List<Directory> path) {
        final var segments = new ArrayList<Tag>();
        final var rootSegment = span(attrs(clazz("segment")),
            userName
        );
        segments.add(rootSegment);
        for (final var directory : path) {
            final var segment = span(attrs(id(directory.id()), clazz("segment")),
                directory.getName()
            );
            segments.add(segment);
        }
        return "/ " + CollectionService.join(" / ", segments);
    }
}
