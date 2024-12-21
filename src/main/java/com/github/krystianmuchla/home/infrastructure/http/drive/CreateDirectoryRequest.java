package com.github.krystianmuchla.home.infrastructure.http.drive;

import com.github.krystianmuchla.home.infrastructure.http.core.RequestBody;

import java.util.UUID;

public record CreateDirectoryRequest(UUID dir, String name) implements RequestBody {
}
