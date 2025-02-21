package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.util.MultiValueHashMap;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;
import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;

import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Time contentsModificationTime
) implements RequestBody {
    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        if (id == null) {
            errors.add("id", ValidationError.nullValue());
        }
        if (contentsModificationTime == null) {
            errors.add("contentsModificationTime", ValidationError.nullValue());
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException();
        }
    }
}
