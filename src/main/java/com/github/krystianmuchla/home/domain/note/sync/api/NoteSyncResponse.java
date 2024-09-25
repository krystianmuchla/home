package com.github.krystianmuchla.home.domain.note.sync.api;

import java.util.List;

public record NoteSyncResponse(List<NoteResponse> notes) {
}
