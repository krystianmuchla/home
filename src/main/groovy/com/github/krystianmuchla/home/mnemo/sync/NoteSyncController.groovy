package com.github.krystianmuchla.home.mnemo.sync

import com.github.krystianmuchla.home.db.DbConnection
import com.github.krystianmuchla.home.db.Transactional
import com.github.krystianmuchla.home.api.RequestReader
import com.github.krystianmuchla.home.api.ResponseWriter
import com.github.krystianmuchla.home.mnemo.Note
import com.github.krystianmuchla.home.mnemo.NoteResponse
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import java.sql.Connection

class NoteSyncController extends HttpServlet implements Transactional {
    static final PATH = '/api/notes/sync'

    private final Connection dbConnection
    private final NoteSyncService noteSyncService

    NoteSyncController() {
        dbConnection = DbConnection.getInstance()
        noteSyncService = NoteSyncService.getInstance(dbConnection)
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final syncNotesRequest = RequestReader.readJson(request, SyncNotesRequest)
        List<Note> notes
        transactional(dbConnection, () -> {
            notes = noteSyncService.sync(fromRequest(syncNotesRequest.notes()))
        })
        ResponseWriter.writeJson(response, new NoteSyncResponse(toResponse(notes)))
    }

    private static List<Note> fromRequest(final List<NoteRequest> notes) {
        return notes.stream().map { new Note(it) }.toList()
    }

    private static List<NoteResponse> toResponse(final List<Note> notes) {
        return notes.stream().map { new NoteResponse(it) }.toList()
    }
}
