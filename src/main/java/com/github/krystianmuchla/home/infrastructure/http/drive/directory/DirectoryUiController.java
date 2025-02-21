package com.github.krystianmuchla.home.infrastructure.http.drive.directory;

import com.github.krystianmuchla.home.application.util.CollectionService;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryNotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.NotFoundException;
import com.github.krystianmuchla.home.infrastructure.http.core.html.Tag;
import com.github.krystianmuchla.home.infrastructure.http.core.html.Tags;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.krystianmuchla.home.infrastructure.http.core.html.Attribute.*;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.div;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tag.span;
import static com.github.krystianmuchla.home.infrastructure.http.core.html.Tags.tags;

public class DirectoryUiController extends Controller {
    public static final DirectoryUiController INSTANCE = new DirectoryUiController();

    private final DirectoryService directoryService = DirectoryService.INSTANCE;

    public DirectoryUiController() {
        super("/ui/drive/directories");
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DirectoryFilterRequest::new);
        List<Directory> hierarchy;
        try {
            hierarchy = directoryService.getHierarchy(user.id, filter.dir());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        }
        List<Directory> list;
        try {
            list = directoryService.list(user.id, filter.dir());
        } catch (DirectoryNotFoundException exception) {
            throw new NotFoundException();
        }
        new ResponseWriter(exchange).html(html(user.name, hierarchy, list)).write();
    }

    // todo ogranij nazwy klas i atrybutów
    private static Tags html(String userName, List<Directory> hierarchy, List<Directory> list) {
        return tags(
            div(attrs(id("dir-path")),
                path(userName, hierarchy)
            ),
            div(attrs(id("dir-list"), clazz("column")),
                tags(list.stream().map(dir ->
                    div(attrs(clazz("dir-dir"), Map.entry("dir-id", dir.id)),
                        dir.name
                    )
                ))
            )
        );
    }

    private static String path(String userName, List<Directory> hierarchy) {
        var segments = new ArrayList<Tag>();
        var rootSegment = span(attrs(clazz("dir-segment")),
            userName
        );
        segments.add(rootSegment);
        for (var dir : hierarchy) {
            var segment = span(attrs(Map.entry("dir-id", dir.id), clazz("dir-segment")),
                dir.name
            );
            segments.add(segment);
        }
        return "/ " + CollectionService.join(" / ", segments);
    }
}
