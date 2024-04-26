package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DriveController extends Controller {
    public static final String PATH = "/api/drive/*";

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final var user = session(request).user();
        final var directories = directories(RequestReader.readPathParameter(request));
        final var fileName = request.getParameter("fileName");
        if (fileName == null) {
            final var list = DriveService.listDirectory(user.id(), directories);
            ResponseWriter.writeJson(response, "[" + String.join(",", list) + "]");
        } else {
            final var file = DriveService.getFile(user.id(), directories, fileName);
            ResponseWriter.writeFile(response, file);
        }
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final var user = session(request).user();
        final var directories = directories(RequestReader.readPathParameter(request));
        final var fileUpload = fileUpload(request);
        DriveService.uploadFile(user.id(), directories, fileUpload);
        response.setStatus(204);
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

    private FileUpload fileUpload(final HttpServletRequest request) {
        final var fileName = request.getHeader("Content-ID");
        if (fileName == null || fileName.isEmpty()) {
            throw new ValidationException("Content-ID", ValidationError.emptyValue());
        }
        return new FileUpload(fileName, RequestReader.inputStream(request));
    }
}
