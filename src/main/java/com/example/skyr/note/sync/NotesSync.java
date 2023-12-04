package com.example.skyr.note.sync;

import java.time.Instant;
import java.util.UUID;

public record NotesSync(UUID syncId, Instant syncTime) {
    public static final String SYNC_ID = "sync_id";
    public static final String SYNC_TIME = "sync_time";
}
