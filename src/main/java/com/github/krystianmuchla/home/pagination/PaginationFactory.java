package com.github.krystianmuchla.home.pagination;

import com.github.krystianmuchla.home.pagination.api.PaginationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginationFactory {
    public static Pagination create(final PaginationRequest paginationRequest) {
        return new Pagination(paginationRequest.getPageNumber(), paginationRequest.getPageSize());
    }
}
