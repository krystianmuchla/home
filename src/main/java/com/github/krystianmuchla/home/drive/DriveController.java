package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DriveController extends Controller {
    public static final String PATH = "/api/drive/*";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final var user = session(request).user();
        final var path = RequestReader.readPathParameter(request);
        final String[] directories;
        if (path == null) {
            directories = new String[]{};
        } else {
            directories = path.split("/");
        }
        final var fileName = request.getParameter("file");
        if (fileName == null) {
            final var list = DriveService.listDirectory(user.id(), directories);
            ResponseWriter.writeJson(response, "[" + String.join(",", list) + "]");
        } else {
            final var file = DriveService.getFile(user.id(), directories, fileName);
            ResponseWriter.writeFile(response, file);
        }
    }
}
