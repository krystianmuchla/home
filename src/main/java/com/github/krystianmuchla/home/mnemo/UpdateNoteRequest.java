package com.github.krystianmuchla.home.mnemo;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import com.github.krystianmuchla.home.util.MultiValueHashMap;

public record UpdateNoteRequest(String title, String content) implements RequestBody {
    @Override
    public void validate() {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        if (title != null && title.length() > Note.TITLE_MAX_LENGTH) {
            errors.add("title", ValidationError.aboveMaxLength(Note.TITLE_MAX_LENGTH));
        }
        if (content != null && content.length() > Note.CONTENT_MAX_LENGTH) {
            errors.add("content", ValidationError.aboveMaxLength(Note.CONTENT_MAX_LENGTH));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
