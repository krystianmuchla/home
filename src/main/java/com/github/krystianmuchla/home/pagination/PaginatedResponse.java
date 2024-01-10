package com.github.krystianmuchla.home.pagination;

import java.util.List;
import java.util.function.Function;

public record PaginatedResponse<T>(List<T> data, PaginationResponse pagination) {
    public <U> PaginatedResponse(final PaginatedResult<U> paginatedResult, final Function<U, T> mapper) {
        this(
            paginatedResult.data().stream().map(mapper).toList(),
            new PaginationResponse(paginatedResult.pagination())
        );
    }
}
