package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.domain.drive.DriveService;
import com.github.krystianmuchla.home.domain.drive.Entry;
import com.github.krystianmuchla.home.domain.drive.EntryType;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.exception.DirectoryNotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.NotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.core.html.Tag;
import com.github.krystianmuchla.home.infrastructure.http.core.html.Tags;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.krystianmuchla.home.application.util.Resource.CONTEXT_MENU_IMAGE;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tags.tags;

public class DriveUiController extends Controller {
    public static final DriveUiController INSTANCE = new DriveUiController();

    public DriveUiController() {
        super("/ui/drive/main");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        List<Directory> dirHierarchy;
        try {
            dirHierarchy = DirectoryService.getHierarchy(user.id, request.dir());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        }
        var list = DriveService.listDirectory(user.id, request.dir());
        ResponseWriter.writeHtml(exchange, 200, html(user.name, dirHierarchy, list));
    }

    private Tags html(String userName, List<Directory> dirHierarchy, List<Entry> list) {
        return tags(
            div(attrs(id("path")), path(userName, dirHierarchy)),
            div(attrs(id("list"), clazz("column")),
                Tags.tags(list.stream().map(entry ->
                    div(attrs(id(entry.id()), clazz("row " + entry.type().asClass())),
                        entryName(entry),
                        entryMenu(entry)
                    )
                ))
            )
        );
    }

    private String path(String userName, List<Directory> dirHierarchy) {
        var segments = new ArrayList<Tag>();
        var rootSegment = span(attrs(clazz("segment")),
            userName
        );
        segments.add(rootSegment);
        for (var directory : dirHierarchy) {
            var segment = span(attrs(id(directory.id), clazz("segment")),
                directory.name
            );
            segments.add(segment);
        }
        return "/ " + CollectionService.join(" / ", segments);
    }

    private Tag entryName(Entry entry) {
        var clazz = switch (entry.type()) {
            case EntryType.DIR -> "dir-name";
            case EntryType.FILE -> "file-name";
        };
        return div(attrs(clazz(clazz)),
            entry.name()
        );
    }

    private Tag entryMenu(Entry entry) {
        var clazz = switch (entry.type()) {
            case EntryType.DIR -> "dir-menu";
            case EntryType.FILE -> "file-menu";
        };
        return div(attrs(clazz(clazz)),
            img(attrs(src(CONTEXT_MENU_IMAGE.urlPath)))
        );
    }
}
