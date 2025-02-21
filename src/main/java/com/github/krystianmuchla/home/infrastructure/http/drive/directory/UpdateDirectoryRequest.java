package com.github.krystianmuchla.home.infrastructure.http.drive.directory;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

import java.util.UUID;

public record UpdateDirectoryRequest(UUID parentId, boolean unsetParentId, String name) implements RequestBody {
}
