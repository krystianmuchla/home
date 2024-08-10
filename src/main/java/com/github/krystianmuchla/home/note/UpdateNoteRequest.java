package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

import java.util.UUID;

public record UpdateNoteRequest(UUID id, String title, String content) implements RequestBody {
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
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }
}
