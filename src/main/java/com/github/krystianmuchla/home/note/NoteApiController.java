package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.exception.RequestException;
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
    protected void delete(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var filter = RequestReader.readQuery(exchange, NoteFilterRequest.class);
        if (filter.isEmpty()) {
            throw new RequestException();
        }
        Transaction.run(() -> NoteService.delete(user.id(), filter.ids()));
        ResponseWriter.write(exchange, 204);
    }

    @Override
    protected void get(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var filter = RequestReader.readQuery(exchange, NoteFilterRequest.class);
        final var pagination = RequestReader.readQuery(exchange, PaginationRequest.class);
        final var result = NoteSql.read(user.id(), filter.ids(), new Pagination(pagination));
        ResponseWriter.writeJson(exchange, 200, new PaginatedResponse<>(result, NoteResponse::new));
    }

    @Override
    protected void post(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readJson(exchange, CreateNoteRequest.class);
        final var noteId = Transaction.run(
            () -> NoteService.create(user.id(), request.title(), request.content())
        );
        ResponseWriter.writeJson(exchange, 201, new IdResponse<>(noteId));
    }

    @Override
    protected void put(final HttpExchange exchange) throws IOException {
        final var user = session(exchange).user();
        final var request = RequestReader.readJson(exchange, UpdateNoteRequest.class);
        Transaction.run(
            () -> NoteService.update(user.id(), request.id(), request.title(), request.content())
        );
        ResponseWriter.write(exchange, 204);
    }
}
