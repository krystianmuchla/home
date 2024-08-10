package com.github.krystianmuchla.home.note;

import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.util.MultiValueMap;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record NoteFilterRequest(Set<UUID> ids) implements RequestQuery {
    public NoteFilterRequest(MultiValueMap<String, String> query) {
        this(resolveId(query));
    }

    public boolean isEmpty() {
        return ids.isEmpty();
    }

    private static Set<UUID> resolveId(MultiValueMap<String, String> query) {
        var id = query.get("id");
        if (id == null) {
            return Set.of();
        }
        try {
            return id.stream().map(UUID::fromString).collect(Collectors.toSet());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("id", ValidationError.wrongFormat());
        }
    }
}
