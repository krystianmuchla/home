package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.note.Note;

import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Time contentsModificationTime) {
    public NoteResponse(Note note) {
        this(note.id, note.title, note.content, note.contentsModificationTime);
    }
}
