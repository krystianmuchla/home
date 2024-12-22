package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Time contentsModificationTime
) implements RequestBody {
}
