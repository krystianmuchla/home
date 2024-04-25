package com.github.krystianmuchla.home.note.sync;

import com.github.krystianmuchla.home.note.NoteResponse;

import java.util.List;

public record NoteSyncResponse(List<NoteResponse> notes) {
}
