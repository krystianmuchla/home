package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.pagination.PaginatedResponse;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

public class NoteController extends Controller {
    public static final String PATH = "/api/notes/*";

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (RequestReader.readPathParameter(request) != null) {
            super.doPost(request, response);
            return;
        }
        final var user = session(request).user();
        final var createNoteRequest = RequestReader.readJson(request, CreateNoteRequest.class);
        final var noteId = Transaction.run(
            () -> NoteService.create(user.id(), createNoteRequest.title(), createNoteRequest.content())
        );
        response.setStatus(201);
        ResponseWriter.writeJson(response, new IdResponse<>(noteId));
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final var user = session(request).user();
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            final var paginationRequest = PaginationRequest.from(request);
            final var paginatedResult = NoteSql.readByUserId(user.id(), new Pagination(paginationRequest));
            ResponseWriter.writeJson(response, new PaginatedResponse<>(paginatedResult, NoteResponse::new));
        } else {
            final var note = NoteService.read(noteId, user.id());
            ResponseWriter.writeJson(response, new NoteResponse(note));
        }
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            super.doPut(request, response);
            return;
        }
        final var user = session(request).user();
        final var updateNoteRequest = RequestReader.readJson(request, UpdateNoteRequest.class);
        Transaction.run(
            () -> NoteService.update(noteId, user.id(), updateNoteRequest.title(), updateNoteRequest.content())
        );
        response.setStatus(204);
    }

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            super.doDelete(request, response);
            return;
        }
        final var user = session(request).user();
        Transaction.run(() -> NoteService.delete(noteId, user.id()));
        response.setStatus(204);
    }
}
