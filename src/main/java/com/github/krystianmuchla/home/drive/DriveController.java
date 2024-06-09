package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.exception.AuthenticationException;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.Tag;
import com.github.krystianmuchla.home.html.element.Listing;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.id.user.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.div;

public class DriveController extends Controller {
    public DriveController() {
        super("/drive");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final User user;
        try {
            user = session(exchange).user();
        } catch (final AuthenticationException exception) {
            ResponseWriter.writeLocation(exchange, "/id/sign_in");
            ResponseWriter.write(exchange, 302);
            return;
        }
        final var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var directories = directories(filter.path());
        final var list = DriveService.listDirectory(user.id(), directories);
        ResponseWriter.writeHtml(exchange, 200, html(list));
    }

    private String[] directories(final String path) {
        final String[] directories;
        if (path == null) {
            directories = new String[]{};
        } else {
            directories = path.split("/");
        }
        return directories;
    }

    private String html(final Set<String> list) {
        final var elements = list.stream().map(element -> div(Map.of(), element)).toArray(Tag[]::new);
        final var listing = new Listing((Object[]) elements);
        return document(
            List.of(Style.BACKGROUND),
            List.of(),
            List.of(listing),
            listing.tag().appendClasses("background")
        );
    }
}
