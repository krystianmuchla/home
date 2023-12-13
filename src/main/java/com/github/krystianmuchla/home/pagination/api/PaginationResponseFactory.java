package com.github.krystianmuchla.home.pagination.api;

import com.github.krystianmuchla.home.pagination.PaginationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationResponseFactory {
    public static PaginationResponse create(final PaginationResult paginationResult) {
        return new PaginationResponse(paginationResult.next(), paginationResult.previous());
    }
}
