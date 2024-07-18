package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.http.Controller;
import com.github.krystianmuchla.home.http.RequestReader;
import com.github.krystianmuchla.home.http.ResponseWriter;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.note.NoteResponse;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NoteSyncApiController extends Controller {
    public NoteSyncApiController() {
        super("/api/notes/sync");
    }

    @Override
    protected void put(final HttpExchange exchange) throws IOException {
        final var user = RequestReader.readUser(exchange);
        final var syncNotesRequest = RequestReader.readJson(exchange, SyncNotesRequest.class);
        final var notes = Transaction.run(
            () -> NoteSyncService.sync(user.id(), map(user.id(), syncNotesRequest.notes()))
        );
        ResponseWriter.writeJson(exchange, 200, new NoteSyncResponse(map(notes)));
    }

    private static List<Note> map(final UUID userId, final List<NoteRequest> notes) {
        return notes.stream().map(noteRequest -> new Note(userId, noteRequest)).toList();
    }

    private static List<NoteResponse> map(final List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
