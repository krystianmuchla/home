package com.github.krystianmuchla.home.drive.controller;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.drive.DriveService;
import com.github.krystianmuchla.home.drive.api.CreateDirectoryRequest;
import com.github.krystianmuchla.home.drive.api.DriveFilterRequest;
import com.github.krystianmuchla.home.drive.api.EntryResponse;
import com.github.krystianmuchla.home.drive.api.UploadFileRequest;
import com.github.krystianmuchla.home.drive.directory.DirectoryService;
import com.github.krystianmuchla.home.drive.file.FileService;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
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
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() != null) {
            Transaction.run(() -> FileService.remove(user.id, request.file()));
        } else if (request.dir() != null) {
            Transaction.run(() -> DirectoryService.remove(user.id, request.dir()));
        } else {
            throw new BadRequestException();
        }
        ResponseWriter.write(exchange, 204);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        if (request.file() == null) {
            var list = DriveService.listDirectory(user.id, request.dir());
            ResponseWriter.writeJson(exchange, 200, list.stream().map(EntryResponse::new).toList());
        } else {
            var fileDto = DriveService.getFile(user.id, request.file());
            ResponseWriter.writeFile(exchange, 200, fileDto.name(), fileDto.file());
        }
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readJson(exchange, CreateDirectoryRequest.class);
        Transaction.run(() -> DirectoryService.create(user.id, request.dir(), request.name()));
        ResponseWriter.write(exchange, 201);
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var filter = RequestReader.readQuery(exchange, DriveFilterRequest::new);
        var request = RequestReader.readHeaders(exchange, UploadFileRequest::new);
        var fileContent = RequestReader.readStream(exchange);
        var fileId = Transaction.run(() -> FileService.create(user.id, filter.dir(), request.fileName()));
        DriveService.uploadFile(user.id, fileId, fileContent);
        Transaction.run(() -> FileService.upload(user.id, fileId));
        ResponseWriter.write(exchange, 204);
    }
}
