package com.github.krystianmuchla.home.drive.controller;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.drive.DriveService;
import com.github.krystianmuchla.home.drive.FileUpload;
import com.github.krystianmuchla.home.drive.api.CreateDirectoryRequest;
import com.github.krystianmuchla.home.drive.api.DriveFilterRequest;
import com.github.krystianmuchla.home.drive.api.EntryResponse;
import com.github.krystianmuchla.home.drive.api.UploadFileRequest;
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
    protected void get(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() == null) {
            final var list = DriveService.listDirectory(user.id(), request.dir());
            ResponseWriter.writeJson(exchange, 200, list.stream().map(EntryResponse::new).toList());
        } else {
            final var file = DriveService.getFile(user.id(), request.file());
            ResponseWriter.writeFile(exchange, 200, file);
        }
    }

    @Override
    protected void post(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readJson(exchange, CreateDirectoryRequest.class);
        Transaction.run(() -> DriveService.createDirectory(user.id(), request.dir(), request.name()));
        ResponseWriter.write(exchange, 201);
    }

    @Override
    protected void put(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        final var request = RequestReader.readHeaders(exchange, UploadFileRequest::new);
        Transaction.run(
            () -> DriveService.uploadFile(
                user.id(),
                filter.dir(),
                new FileUpload(request.fileName(), RequestReader.readStream(exchange))
            )
        );
        ResponseWriter.write(exchange, 204);
    }
}
