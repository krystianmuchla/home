package com.example.skyr.pagination.api;

import com.example.skyr.pagination.PaginationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationResponseFactory {
    public static PaginationResponse create(final PaginationResult paginationResult) {
        return new PaginationResponse(paginationResult.next(), paginationResult.previous());
    }
}
