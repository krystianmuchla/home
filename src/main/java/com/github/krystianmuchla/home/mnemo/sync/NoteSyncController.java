package com.github.krystianmuchla.home.mnemo.sync;

import java.util.List;

import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteResponse;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class NoteSyncController extends HttpServlet {
    public static final String PATH = "/api/notes/sync";

    private final NoteSyncService noteSyncService = NoteSyncService.INSTANCE;

    @Override
    @SneakyThrows
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final var syncNotesRequest = RequestReader.readJson(request, SyncNotesRequest.class);
        final var notes = Transaction.run(
            () -> noteSyncService.sync(fromRequest(syncNotesRequest.notes()))
        );
        ResponseWriter.writeJson(response, new NoteSyncResponse(toResponse(notes)));
    }

    private static List<Note> fromRequest(final List<NoteRequest> notes) {
        return notes.stream().map(Note::new).toList();
    }

    private static List<NoteResponse> toResponse(final List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
