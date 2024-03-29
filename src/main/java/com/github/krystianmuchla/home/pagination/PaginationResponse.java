package com.github.krystianmuchla.home.pagination;

public record PaginationResponse(Boolean next, Boolean previous) {
    public PaginationResponse(final PaginationResult paginationResult) {
        this(paginationResult.next(), paginationResult.previous());
    }
}
