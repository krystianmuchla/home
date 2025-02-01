package com.github.krystianmuchla.home.infrastructure.http.drive.file;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

public record UpdateFileRequest(String name) implements RequestBody {
}
