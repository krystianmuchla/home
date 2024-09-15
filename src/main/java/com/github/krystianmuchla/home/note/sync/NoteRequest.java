package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.note.Note;
import com.github.krystianmuchla.home.util.InstantFactory;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

import java.time.Instant;
import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Instant contentsModificationTime
) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        if (id == null) {
            errors.add("id", ValidationError.nullValue());
        }
        if (title != null && title.length() > Note.TITLE_MAX_LENGTH) {
            errors.add("title", ValidationError.aboveMaxLength(Note.TITLE_MAX_LENGTH));
        }
        if (content != null && content.length() > Note.CONTENT_MAX_LENGTH) {
            errors.add("content", ValidationError.aboveMaxLength(Note.CONTENT_MAX_LENGTH));
        }
        if (contentsModificationTime == null) {
            errors.add("contentsModificationTime", ValidationError.nullValue());
        } else if (InstantFactory.create(contentsModificationTime) != contentsModificationTime) {
            errors.add("contentsModificationTime", ValidationError.wrongFormat());
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
