package com.github.krystianmuchla.home.note.sync;

import java.util.List;

public record NoteSyncResponse(List<NoteResponse> notes) {
}
