package com.github.krystianmuchla.home.domain.drive.directory;

import java.util.UUID;

public record DirectoryUpdate(UUID parentId, boolean unsetParentId, String name) {
}
