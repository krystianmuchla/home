package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.validation.ValidationError;
import com.github.krystianmuchla.home.exception.validation.ValidationException;

public record CreateNoteRequest(String title, String content) implements RequestBody {
    @Override
    public void validate() {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        if (title == null) {
            errors.add("title", ValidationError.nullValue());
        } else if (title.length() > Note.TITLE_MAX_LENGTH) {
            errors.add("title", ValidationError.aboveMaxLength(Note.TITLE_MAX_LENGTH));
        }
        if (content == null) {
            errors.add("content", ValidationError.nullValue());
        } else if (content.length() > Note.CONTENT_MAX_LENGTH) {
            errors.add("content", ValidationError.aboveMaxLength(Note.CONTENT_MAX_LENGTH));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
