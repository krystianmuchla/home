package com.github.krystianmuchla.home.mnemo.sync;

import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.DbConnection;
import com.github.krystianmuchla.home.db.Transactional;
import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class NoteSyncController extends HttpServlet implements Transactional {
    public static final String PATH = "/api/notes/sync";

    private final Connection dbConnection;
    private final NoteSyncService noteSyncService;

    public NoteSyncController() {
        dbConnection = DbConnection.getInstance();
        noteSyncService = NoteSyncService.getInstance(dbConnection);
    }

    @Override
    @SneakyThrows
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final var syncNotesRequest = RequestReader.readJson(request, SyncNotesRequest.class);
        final var notes = new AtomicReference<List<Note>>(null);
        transactional(dbConnection, () -> {
            notes.set(noteSyncService.sync(fromRequest(syncNotesRequest.notes())));
        });
        ResponseWriter.writeJson(response, new NoteSyncResponse(toResponse(notes.get())));
    }

    private static List<Note> fromRequest(final List<NoteRequest> notes) {
        return notes.stream().map(Note::new).toList();
    }

    private static List<NoteResponse> toResponse(final List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
