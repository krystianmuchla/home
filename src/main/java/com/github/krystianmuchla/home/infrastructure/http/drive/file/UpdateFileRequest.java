package com.github.krystianmuchla.home.infrastructure.http.drive.file;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

import java.util.UUID;

public record UpdateFileRequest(UUID directoryId, boolean unsetDirectoryId, String name) implements RequestBody {
}
