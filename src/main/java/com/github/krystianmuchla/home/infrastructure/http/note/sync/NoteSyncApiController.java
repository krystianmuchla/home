package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.domain.note.Note;
import com.github.krystianmuchla.home.domain.note.NoteSyncService;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationError;
import com.github.krystianmuchla.home.domain.note.error.NoteValidationException;
import com.github.krystianmuchla.home.infrastructure.http.core.Controller;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestReader;
import com.github.krystianmuchla.home.infrastructure.http.core.ResponseWriter;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.InternalServerErrorException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NoteSyncApiController extends Controller {
    public static final NoteSyncApiController INSTANCE = new NoteSyncApiController();

    private final NoteSyncService noteSyncService = NoteSyncService.INSTANCE;

    public NoteSyncApiController() {
        super("/api/notes/sync");
    }

    @Override
    protected void put(HttpExchange exchange) throws IOException {
        var user = RequestReader.readUser(exchange);
        var syncNotesRequest = RequestReader.readJson(exchange, SyncNotesRequest.class);
        var notes = noteSyncService.sync(user.id, map(user.id, syncNotesRequest.notes()));
        new ResponseWriter(exchange).json(new NoteSyncResponse(map(notes))).write();
    }

    private static List<Note> map(UUID userId, List<NoteRequest> notes) {
        return notes.stream().map(note -> {
            try {
                return new Note(note.id(), userId, note.title(), note.content(), note.contentsModificationTime());
            } catch (NoteValidationException exception) {
                var errors = new MultiValueHashMap<String, ValidationError>();
                for (var error : exception.errors) {
                    switch (error) {
                        case NoteValidationError.NullId ignored -> errors.add("id", ValidationError.nullValue());
                        case NoteValidationError.TitleAboveMaxLength e ->
                            errors.add("title", ValidationError.aboveMaxLength(e.maxLength));
                        case NoteValidationError.ContentAboveMaxLength e ->
                            errors.add("content", ValidationError.aboveMaxLength(e.maxLength));
                        case NoteValidationError.NullContentsModificationTime ignored ->
                            errors.add("contentsModificationTime", ValidationError.nullValue());
                        default -> {
                        }
                    }
                }
                if (errors.isEmpty()) {
                    throw new InternalServerErrorException(exception);
                } else {
                    throw new BadRequestException(errors);
                }
            }
        }).toList();
    }

    private static List<NoteResponse> map(List<Note> notes) {
        return notes.stream().map(NoteResponse::new).toList();
    }
}
