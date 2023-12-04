package com.example.skyr.note.sync.api;

import com.example.skyr.note.Note;
import com.example.skyr.note.api.NoteResponse;
import com.example.skyr.note.api.NoteResponseFactory;
import com.example.skyr.note.sync.NotesSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class NotesSyncController {
    private final NotesSyncService notesSyncService;

    @PutMapping(
            value = "/api/notes/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public NotesSyncResponse syncNotes(@RequestBody final SyncNotesRequest request) {
        final var notesSyncResult = notesSyncService.sync(
                request.syncId,
                request.syncTime.toInstant(),
                map(request.modifiedNotes)
        );
        return new NotesSyncResponse(
                notesSyncResult.syncId(),
                notesSyncResult.syncTime(),
                NoteResponseFactory.create(notesSyncResult.modifiedNotes())
        );
    }

    private List<Note> map(final List<NoteRequest> notes) {
        return notes.stream().map(note -> new Note(
                note.id,
                note.title,
                note.content,
                note.creationTime.toInstant(),
                note.modificationTime.toInstant())
        ).toList();
    }

    public record SyncNotesRequest(UUID syncId, ZonedDateTime syncTime, List<NoteRequest> modifiedNotes) {
    }

    public record NoteRequest(UUID id,
                              String title,
                              String content,
                              ZonedDateTime creationTime,
                              ZonedDateTime modificationTime) {
    }

    public record NotesSyncResponse(UUID syncId, Instant syncTime, List<NoteResponse> modifiedNotes) {
    }
}
