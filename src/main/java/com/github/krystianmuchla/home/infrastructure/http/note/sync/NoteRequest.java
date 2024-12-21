package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

import java.time.Instant;
import java.util.UUID;

public record NoteRequest(
    UUID id,
    String title,
    String content,
    Instant contentsModificationTime
) implements RequestBody {
}
