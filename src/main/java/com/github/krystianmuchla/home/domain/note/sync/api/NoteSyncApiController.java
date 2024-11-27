package com.github.krystianmuchla.home.domain.note.sync.api;

import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.domain.note.sync.NoteSyncService;
import com.github.krystianmuchla.home.infrastructure.http.Controller;
import com.github.krystianmuchla.home.infrastructure.http.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.persistence.Transaction;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NoteSyncApiController extends Controller {
    public static final NoteSyncApiController INSTANCE = new NoteSyncApiController();

    public NoteSyncApiController() {
        super("/api/notes/sync");
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var syncNotesRequest = RequestReader.readJson(exchange, SyncNotesRequest.class);
        var notes = Transaction.run(
            () -> NoteSyncService.sync(user.id, map(user.id, syncNotesRequest.notes()))
        );
        ResponseWriter.writeJson(exchange, 200, new NoteSyncResponse(map(notes)));
    }

    private static List<Note> map(UUID userId, List<NoteRequest> notes) {
        return notes.stream().map(noteRequest -> new Note(userId, noteRequest)).toList();
    }

    private static List<NoteResponse> map(List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
