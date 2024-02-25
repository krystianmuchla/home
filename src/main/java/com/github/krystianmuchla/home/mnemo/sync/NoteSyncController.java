package com.github.krystianmuchla.home.mnemo.sync;

import java.util.List;
import java.util.UUID;

import com.github.krystianmuchla.home.api.Controller;
import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.api.ResponseWriter;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.mnemo.Note;
import com.github.krystianmuchla.home.mnemo.NoteResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public class NoteSyncController extends Controller {
    public static final String PATH = "/api/notes/sync";

    private final NoteSyncService noteSyncService = NoteSyncService.INSTANCE;

    @Override
    @SneakyThrows
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) {
        final var user = sessionData(request).user();
        final var syncNotesRequest = RequestReader.readJson(request, SyncNotesRequest.class);
        final var notes = Transaction.run(
                () -> noteSyncService.sync(user.id(), map(user.id(), syncNotesRequest.notes())));
        ResponseWriter.writeJson(response, new NoteSyncResponse(map(notes)));
    }

    private static List<Note> map(final UUID userId, final List<NoteRequest> notes) {
        return notes.stream().map(noteRequest -> new Note(userId, noteRequest)).toList();
    }

    private static List<NoteResponse> map(final List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
