package com.github.krystianmuchla.skyr.note.sync.api;

import com.github.krystianmuchla.skyr.note.Note;
import com.github.krystianmuchla.skyr.note.api.NoteResponse;
import com.github.krystianmuchla.skyr.note.api.NoteResponseFactory;
import com.github.krystianmuchla.skyr.note.sync.NoteSyncService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public final class NoteSyncController {
    private final NoteSyncService noteSyncService;

    @Validated
    @PutMapping(
            value = "/api/notes/sync",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public NoteSyncResponse syncNotes(@Valid @RequestBody final SyncNotesRequest request) {
        final var notesSyncResult = noteSyncService.sync(
                request.syncId,
                map(request.modifiedNotes)
        );
        return new NoteSyncResponse(
                notesSyncResult.syncId(),
                NoteResponseFactory.create(notesSyncResult.modifiedNotes())
        );
    }

    private List<Note> map(final List<NoteRequest> notes) {
        if (notes == null) return List.of();
        return notes.stream().map(note -> new Note(
                note.id,
                note.title,
                note.content,
                note.creationTime.toInstant(),
                note.modificationTime.toInstant())
        ).toList();
    }

    public record SyncNotesRequest(@NotNull UUID syncId, @Valid List<NoteRequest> modifiedNotes) {
    }

    public record NoteRequest(@NotNull UUID id,
                              @NotNull String title,
                              String content,
                              @NotNull ZonedDateTime creationTime,
                              @NotNull ZonedDateTime modificationTime) {
    }

    public record NoteSyncResponse(UUID syncId, List<NoteResponse> modifiedNotes) {
    }
}
