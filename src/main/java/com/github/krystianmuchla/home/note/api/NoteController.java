package com.github.krystianmuchla.home.note.api;

import com.github.krystianmuchla.home.api.IdResponse;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.note.NoteService;
import com.github.krystianmuchla.home.pagination.PaginationFactory;
import com.github.krystianmuchla.home.pagination.api.PaginatedResponse;
import com.github.krystianmuchla.home.pagination.api.PaginatedResponseFactory;
import com.github.krystianmuchla.home.pagination.api.PaginationRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @Validated
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/api/notes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public IdResponse<UUID> postNote(@Valid @RequestBody final AddNoteRequest request) {
        final var noteId = noteService.add(request.title, request.content);
        return new IdResponse<>(noteId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/notes/{noteId}")
    public void deleteNote(@PathVariable final UUID noteId) {
        noteService.remove(noteId);
    }

    @GetMapping(value = "/api/notes/{noteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public NoteResponse getNote(@PathVariable final UUID noteId) {
        final var note = noteService.get(noteId);
        return NoteResponse.builder()
                .title(note.title())
                .content(note.content())
                .creationTime(note.creationTime())
                .modificationTime(note.modificationTime())
                .build();
    }

    @GetMapping(value = "/api/notes", produces = MediaType.APPLICATION_JSON_VALUE)
    public PaginatedResponse<NoteResponse> getNotes(final PaginationRequest request) {
        final var paginatedResult = noteService.get(PaginationFactory.create(request));
        return PaginatedResponseFactory.create(paginatedResult, NoteResponseFactory::create);
    }

    @Validated
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/api/notes/{noteId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void putNote(@PathVariable final UUID noteId, @Valid @RequestBody UpdateNoteRequest request) {
        noteService.update(noteId, request.title, request.content);
    }

    public record AddNoteRequest(@NotNull @Size(max = Note.TITLE_MAX_LENGTH) String title,
                                 @NotNull @Size(max = Note.CONTENT_MAX_LENGTH) String content) {
    }

    public record UpdateNoteRequest(@Size(max = Note.TITLE_MAX_LENGTH) String title,
                                    @Size(max = Note.CONTENT_MAX_LENGTH) String content) {
    }
}
