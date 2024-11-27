package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import java.util.List;

public record NoteSyncResponse(List<NoteResponse> notes) {
}
