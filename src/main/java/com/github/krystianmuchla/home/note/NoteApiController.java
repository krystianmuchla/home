package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.pagination.PaginatedResponse;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationRequest;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class NoteApiController extends Controller {
    public NoteApiController() {
        super("/api/notes");
    }

    @Override
    protected void delete(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, NoteFilterRequest::new);
        Transaction.run(() -> NoteService.delete(user.id(), request));
        ResponseWriter.write(exchange, 204);
    }

    @Override
    protected void get(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readQuery(exchange, NoteFilterRequest::new);
        var pagination = RequestReader.readQuery(exchange, PaginationRequest::new);
        var result = NotePersistence.read(user.id(), request.ids(), new Pagination(pagination));
        ResponseWriter.writeJson(exchange, 200, new PaginatedResponse<>(result, NoteResponse::new));
    }

    @Override
    protected void post(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readJson(exchange, CreateNoteRequest.class);
        var noteId = Transaction.run(() -> NoteService.create(user.id(), request));
        ResponseWriter.writeJson(exchange, 201, new IdResponse<>(noteId));
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var request = RequestReader.readJson(exchange, UpdateNoteRequest.class);
        Transaction.run(() -> NoteService.update(user.id(), request));
        ResponseWriter.write(exchange, 204);
    }
}
