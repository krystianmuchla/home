package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class DriveApiController extends Controller {
    public DriveApiController() {
        super("/api/drive");
    }

    @Override
    protected void put(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var directories = directories(filter.path());
        DriveService.uploadFile(user.id(), directories, fileUpload(exchange));
        ResponseWriter.write(exchange, 204);
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

    private FileUpload fileUpload(final HttpExchange exchange) {
        final var fileName = RequestReader.readHeader(exchange, "Content-ID");
        if (fileName == null || fileName.isEmpty()) {
            throw new BadRequestException("Content-ID", ValidationError.emptyValue());
        }
        return new FileUpload(fileName, RequestReader.readStream(exchange));
    }
}
