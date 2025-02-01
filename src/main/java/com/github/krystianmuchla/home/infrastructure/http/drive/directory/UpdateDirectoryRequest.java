package com.github.krystianmuchla.home.infrastructure.http.drive.directory;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

public record UpdateDirectoryRequest(String name) implements RequestBody {
}
