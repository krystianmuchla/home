package com.github.krystianmuchla.home.mnemo.sync;

import com.github.krystianmuchla.home.api.RequestBody;

import java.util.List;

public record SyncNotesRequest(List<NoteRequest> notes) implements RequestBody {
    @Override
    public void validate() {
        if (notes == null) throw new IllegalArgumentException();
    }
}
