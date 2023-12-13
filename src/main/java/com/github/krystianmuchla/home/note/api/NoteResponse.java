package com.github.krystianmuchla.home.note.api;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NoteResponse(UUID id, String title, String content, Instant creationTime, Instant modificationTime) {
}
