package com.github.krystianmuchla.home.note;

import java.time.Instant;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Instant creationTime, Instant modificationTime) {
    public NoteResponse(final Note note) {
        this(note.id(), note.title(), note.content(), note.creationTime(), note.modificationTime());
    }
}
