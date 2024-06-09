package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.api.RequestBody;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.exception.ValidationError;

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
