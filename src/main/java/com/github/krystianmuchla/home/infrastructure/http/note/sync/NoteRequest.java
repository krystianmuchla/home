package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.core.exception.BadRequestException;

import java.time.Instant;
import java.util.UUID;

import static com.github.krystianmuchla.home.domain.note.NoteValidator.*;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Instant contentsModificationTime
) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        errors.addAll("id", validateNoteId(id));
        if (title != null) {
            errors.addAll("title", validateNoteTitle(title));
        }
        if (content != null) {
            errors.addAll("content", validateNoteContent(content));
        }
        errors.addAll("contentsModificationTime", validateNoteContentsModificationTime(contentsModificationTime));
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
