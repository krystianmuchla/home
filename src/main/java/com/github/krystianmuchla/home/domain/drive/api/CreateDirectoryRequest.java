package com.github.krystianmuchla.home.domain.drive.api;

import com.github.krystianmuchla.home.application.exception.ValidationError;
import com.github.krystianmuchla.home.infrastructure.http.api.RequestBody;
import com.github.krystianmuchla.home.infrastructure.http.exception.BadRequestException;

import java.util.UUID;

public record CreateDirectoryRequest(UUID dir, String name) implements RequestBody {
    @Override
    public void validate() {
        if (name == null) {
            throw new BadRequestException("name", ValidationError.nullValue());
        }
        if (name.isBlank()) {
            throw new BadRequestException("name", ValidationError.emptyValue());
        }
    }
}
