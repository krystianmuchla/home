package com.example.skyr.pagination;

import com.example.skyr.pagination.api.PaginationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationFactory {
    public static Pagination create(final PaginationRequest paginationRequest) {
        return new Pagination(paginationRequest.getPageNumber(), paginationRequest.getPageSize());
    }
}
