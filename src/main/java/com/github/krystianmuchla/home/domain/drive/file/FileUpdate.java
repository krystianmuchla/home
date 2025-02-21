package com.github.krystianmuchla.home.domain.drive.file;

import java.util.UUID;

public record FileUpdate(UUID directoryId, boolean unsetDirectoryId, String name) {
}
