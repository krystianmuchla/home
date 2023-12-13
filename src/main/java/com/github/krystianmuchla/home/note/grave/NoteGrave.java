package com.github.krystianmuchla.home.note.grave;

import com.github.krystianmuchla.home.note.Note;

import java.time.Instant;
import java.util.UUID;

public record NoteGrave(UUID id, Instant creationTime) {
    public static final String ID = "id";
    public static final String CREATION_TIME = "creation_time";

    public Note toNote() {
        return Note.builder()
                .id(id)
                .modificationTime(creationTime)
                .build();
    }
}
