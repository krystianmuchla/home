package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.domain.drive.Entry;
import com.github.krystianmuchla.home.domain.drive.EntryType;

import java.util.UUID;

public record EntryResponse(UUID id, EntryType type, String name) {
    public EntryResponse(Entry entry) {
        this(entry.id(), entry.type(), entry.name());
    }
}
