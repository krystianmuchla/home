package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.infrastructure.http.core.error.ValidationError;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.core.error.BadRequestException;

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
