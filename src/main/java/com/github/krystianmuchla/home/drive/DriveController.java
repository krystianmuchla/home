package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import com.github.krystianmuchla.home.html.Style;
import com.github.krystianmuchla.home.html.Tag;
import com.github.krystianmuchla.home.html.element.Listing;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.krystianmuchla.home.html.Html.document;
import static com.github.krystianmuchla.home.html.Tag.div;

public class DriveController extends Controller {
    public static final String PATH = "/drive/*";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        try {
            final var user = session(request).user();
            final var directories = directories(RequestReader.readPathParameter(request));
            final var list = DriveService.listDirectory(user.id(), directories);
            ResponseWriter.writeHtml(response, html(list));
        } catch (final AuthenticationException exception) {
            response.sendRedirect("/id/sign_in");
        }
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
