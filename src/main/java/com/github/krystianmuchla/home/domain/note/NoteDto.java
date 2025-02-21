package com.github.krystianmuchla.home.domain.note;

import com.github.krystianmuchla.home.application.time.Time;

import java.util.UUID;

public record NoteDto(UUID id, String title, String content, Time contentsModificationTime) {
    public NoteDto {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (contentsModificationTime == null) {
            throw new IllegalArgumentException("Contents modification time cannot be null");
        }
    }

    public NoteDto(UUID id, Time contentsModificationTime) {
        this(id, null, null, contentsModificationTime);
    }

    boolean hasContent() {
        return content != null;
    }
}
