package com.github.krystianmuchla.home.drive;

import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;

public class DriveApiController extends Controller {
    public DriveApiController() {
        super("/api/drive");
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() == null) {
            final var list = DriveService.listDirectory(user.id(), request.dir());
            ResponseWriter.writeJson(exchange, 200, list.stream().map(File::getName).toList());
        } else {
            final var file = DriveService.getFile(user.id(), request.dir(), request.file());
            ResponseWriter.writeFile(exchange, 200, file);
        }
    }

    @Override
    protected void put(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var file = request.file();
        if (file == null || file.isBlank()) {
            throw new BadRequestException("file", ValidationError.emptyValue());
        }
        DriveService.uploadFile(user.id(), request.dir(), new FileUpload(file, RequestReader.readStream(exchange)));
        ResponseWriter.write(exchange, 204);
    }
}
