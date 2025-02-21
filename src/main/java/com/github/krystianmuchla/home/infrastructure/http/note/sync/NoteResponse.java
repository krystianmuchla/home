package com.github.krystianmuchla.home.infrastructure.http.note.sync;

import com.github.krystianmuchla.home.application.time.Time;

import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Time contentsModificationTime) {
}
