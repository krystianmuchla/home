package com.github.krystianmuchla.home.mnemo;

import java.io.IOException;
import java.util.UUID;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.pagination.PaginatedResponse;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class NoteController extends Controller {
    public static final String PATH = "/api/notes/*";

    private final NoteDao noteDao = NoteDao.INSTANCE;
    private final NoteService noteService = NoteService.INSTANCE;

    @Override
    @SneakyThrows
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        if (RequestReader.readPathParameter(request) != null) {
            super.doPost(request, response);
            return;
        }
        final var user = sessionData(request).user();
        final var createNoteRequest = RequestReader.readJson(request, CreateNoteRequest.class);
        final var noteId = Transaction.run(
            () -> noteService.create(user.id(), createNoteRequest.title(), createNoteRequest.content())
        );
        ResponseWriter.writeJson(response, new IdResponse<>(noteId));
        response.setStatus(201);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final var user = sessionData(request).user();
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            final var paginationRequest = new PaginationRequest(request);
            final var paginatedResult = noteDao.read(user.id(), new Pagination(paginationRequest));
            ResponseWriter.writeJson(response, new PaginatedResponse<>(paginatedResult, NoteResponse::new));
        } else {
            final var note = noteDao.read(noteId, user.id());
            ResponseWriter.writeJson(response, new NoteResponse(note));
        }
    }

    @Override
    @SneakyThrows
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            super.doPut(request, response);
            return;
        }
        final var user = sessionData(request).user();
        final var updateNoteRequest = RequestReader.readJson(request, UpdateNoteRequest.class);
        Transaction.run(
            () -> noteService.update(noteId, user.id(), updateNoteRequest.title(), updateNoteRequest.content())
        );
        response.setStatus(204);
    }

    @Override
    @SneakyThrows
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) {
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            super.doDelete(request, response);
            return;
        }
        final var user = sessionData(request).user();
        Transaction.run(() -> noteService.delete(noteId, user.id()));
        response.setStatus(204);
    }
}
