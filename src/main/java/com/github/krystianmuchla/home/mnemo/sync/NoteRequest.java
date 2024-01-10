package com.github.krystianmuchla.home.mnemo.sync;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.mnemo.Note;

import java.time.ZonedDateTime;
import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    ZonedDateTime creationTime,
    ZonedDateTime modificationTime
) implements RequestBody {
    @Override
    public void validate() {
        if (id == null) throw new IllegalArgumentException();
        if (title != null && title.length() > Note.TITLE_MAX_LENGTH) throw new IllegalArgumentException();
        if (content != null && content.length() > Note.CONTENT_MAX_LENGTH) throw new IllegalArgumentException();
        if (modificationTime == null) throw new IllegalArgumentException();
    }
}
