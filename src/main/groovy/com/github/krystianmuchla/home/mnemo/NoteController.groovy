package com.github.krystianmuchla.home.mnemo

import com.github.krystianmuchla.home.db.DbConnection
import com.github.krystianmuchla.home.db.Transactional
import com.github.krystianmuchla.home.api.IdResponse
import com.github.krystianmuchla.home.api.RequestReader
import com.github.krystianmuchla.home.api.ResponseWriter
import com.github.krystianmuchla.home.pagination.PaginatedResponse
import com.github.krystianmuchla.home.pagination.Pagination
import com.github.krystianmuchla.home.pagination.PaginationRequest
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.sql.Connection

class NoteController extends HttpServlet implements Transactional {
    static final PATH = '/api/notes/*'

    private final Connection dbConnection
    private final NoteDao noteDao
    private final NoteService noteService

    NoteController() {
        dbConnection = DbConnection.getInstance()
        noteDao = NoteDao.getInstance(dbConnection)
        noteService = NoteService.getInstance(dbConnection)
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        if (RequestReader.readPathParameter(request) != null) {
            super.doPost(request, response)
            return
        }
        final createNoteRequest = RequestReader.readJson(request, CreateNoteRequest)
        UUID noteId
        transactional(dbConnection, () -> {
            noteId = noteService.create(createNoteRequest.title(), createNoteRequest.content())
        })
        ResponseWriter.writeJson(response, new IdResponse(noteId))
        response.setStatus(201)
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final noteId = RequestReader.readPathParameter(request, UUID::fromString)
        if (noteId == null) {
            final paginationRequest = new PaginationRequest(request)
            final paginatedResult = noteDao.read(new Pagination(paginationRequest))
            ResponseWriter.writeJson(response, new PaginatedResponse<>(paginatedResult, NoteResponse::new))
        } else {
            final note = noteDao.read(noteId)
            ResponseWriter.writeJson(response, new NoteResponse(note))
        }
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final noteId = RequestReader.readPathParameter(request, UUID::fromString)
        if (noteId == null) {
            super.doPut(request, response)
            return
        }
        final updateNoteRequest = RequestReader.readJson(request, UpdateNoteRequest)
        transactional(dbConnection, () -> {
            noteService.update(noteId, updateNoteRequest.title(), updateNoteRequest.content())
        })
        response.setStatus(204)
    }

    @Override
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) {
        final noteId = RequestReader.readPathParameter(request, UUID::fromString)
        if (noteId == null) {
            super.doDelete(request, response)
            return
        }
        transactional(dbConnection, () -> {
            noteService.delete(noteId)
        })
        response.setStatus(204)
    }
}
