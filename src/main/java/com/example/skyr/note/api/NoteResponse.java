package com.example.skyr.note.api;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record NoteResponse(UUID id, String title, String content, Instant creationTime, Instant modificationTime) {
}
