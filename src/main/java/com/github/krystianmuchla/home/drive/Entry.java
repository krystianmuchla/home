package com.github.krystianmuchla.home.drive;

import java.util.UUID;

public record Entry(UUID id, EntryType type, String name) {
}
