package com.github.krystianmuchla.home.domain.note.sync.api;

import com.github.krystianmuchla.home.domain.note.Note;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Instant contentsModificationTime) {
    public NoteResponse(Note note) {
        this(note.id, note.title, note.content, note.contentsModificationTime);
    }
}
