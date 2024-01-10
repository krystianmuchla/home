package com.github.krystianmuchla.home.mnemo.sync;

import com.github.krystianmuchla.home.mnemo.NoteResponse;

import java.util.List;

public record NoteSyncResponse(List<NoteResponse> notes) {
}
