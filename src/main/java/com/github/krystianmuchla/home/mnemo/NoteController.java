package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.DbConnection;
import com.github.krystianmuchla.home.db.Transactional;
import com.github.krystianmuchla.home.pagination.PaginatedResponse;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationRequest;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

public class NoteController extends HttpServlet implements Transactional {
    public static final String PATH = "/api/notes/*";

    private final Connection dbConnection;
    private final NoteDao noteDao;
    private final NoteService noteService;

    public NoteController() {
        dbConnection = DbConnection.getInstance();
        noteDao = NoteDao.getInstance(dbConnection);
        noteService = NoteService.getInstance(dbConnection);
    }

    @Override
    @SneakyThrows
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        if (RequestReader.readPathParameter(request) != null) {
            super.doPost(request, response);
            return;
        }
        final var createNoteRequest = RequestReader.readJson(request, CreateNoteRequest.class);
        final var noteId = transactional(
            dbConnection,
            () -> noteService.create(createNoteRequest.title(), createNoteRequest.content())
        );
        ResponseWriter.writeJson(response, new IdResponse<>(noteId));
        response.setStatus(201);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final var noteId = RequestReader.readPathParameter(request, UUID::fromString);
        if (noteId == null) {
            final var paginationRequest = new PaginationRequest(request);
            final var paginatedResult = noteDao.read(new Pagination(paginationRequest));
            ResponseWriter.writeJson(response, new PaginatedResponse<>(paginatedResult, NoteResponse::new));
        } else {
            final var note = noteDao.read(noteId);
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
        final var updateNoteRequest = RequestReader.readJson(request, UpdateNoteRequest.class);
        transactional(dbConnection, () -> {
            noteService.update(noteId, updateNoteRequest.title(), updateNoteRequest.content());
        });
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
        transactional(dbConnection, () -> {
            noteService.delete(noteId);
        });
        response.setStatus(204);
    }
}
