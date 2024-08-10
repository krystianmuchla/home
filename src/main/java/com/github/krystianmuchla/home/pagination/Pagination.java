package com.github.krystianmuchla.home.pagination;

import com.github.krystianmuchla.home.exception.InternalException;

public record Pagination(int pageNumber, int pageSize) {
    public static final int MIN_PAGE_NUMBER = 1;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 1000;

    public Pagination {
        if (pageNumber < MIN_PAGE_NUMBER) {
            throw new InternalException("Page number exceeded min length of " + MIN_PAGE_NUMBER);
        }
        if (pageSize < MIN_PAGE_SIZE) {
            throw new InternalException("Page size exceeded min length of " + MIN_PAGE_SIZE);
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new InternalException("Page size exceeded max length of " + MAX_PAGE_SIZE);
        }
    }

    public Pagination(PaginationRequest request) {
        this(request.pageNumber(), request.pageSize());
    }
}
