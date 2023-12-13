package com.github.krystianmuchla.home.pagination.api;

import com.github.krystianmuchla.home.pagination.PaginatedResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginatedResponseFactory {
    public static <T, U> PaginatedResponse<T> create(final PaginatedResult<U> paginatedResult, final Function<U, T> mapper) {
        return new PaginatedResponse<>(
                paginatedResult.data().stream().map(mapper).toList(),
                PaginationResponseFactory.create(paginatedResult.pagination())
        );
    }
}
