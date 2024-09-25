package com.github.krystianmuchla.home.domain.note.sync.api;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

import java.util.List;

public record SyncNotesRequest(List<NoteRequest> notes) implements RequestBody {
    @Override
    public void validate() {
        if (notes == null) {
            throw new BadRequestException("notes", ValidationError.nullValue());
        } else {
            notes.forEach(NoteRequest::validate);
        }
    }
}
