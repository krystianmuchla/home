package com.github.krystianmuchla.home.drive.api;

import com.github.krystianmuchla.home.drive.Entry;
import com.github.krystianmuchla.home.drive.EntryType;

import java.util.UUID;

public record EntryResponse(UUID id, EntryType type, String name) {
    public EntryResponse(Entry entry) {
        this(entry.id(), entry.type(), entry.name());
    }
}
