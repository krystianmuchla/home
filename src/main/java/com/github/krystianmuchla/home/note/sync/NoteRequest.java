package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

import java.time.Instant;
import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Instant creationTime,
    Instant modificationTime
) implements RequestBody {
    @Override
    public void validate() {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        if (id == null) {
            errors.add("id", ValidationError.nullValue());
        }
        if (title != null && title.length() > Note.TITLE_MAX_LENGTH) {
            errors.add("title", ValidationError.aboveMaxLength(Note.TITLE_MAX_LENGTH));
        }
        if (content != null && content.length() > Note.CONTENT_MAX_LENGTH) {
            errors.add("content", ValidationError.aboveMaxLength(Note.CONTENT_MAX_LENGTH));
        }
        if (modificationTime == null) {
            errors.add("modificationTime", ValidationError.nullValue());
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
