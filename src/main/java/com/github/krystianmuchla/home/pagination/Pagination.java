package com.github.krystianmuchla.home.pagination;

public record Pagination(int pageNumber, int pageSize) {
    public static final int MIN_PAGE_NUMBER = 1;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 1000;

    public Pagination {
        if (pageNumber < MIN_PAGE_NUMBER) {
            throw new IllegalArgumentException("Page number exceeded min length of " + MIN_PAGE_NUMBER);
        }
        if (pageSize < MIN_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size exceeded min length of " + MIN_PAGE_SIZE);
        }
        if (pageSize > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size exceeded max length of " + MAX_PAGE_SIZE);
        }
    }

    public Pagination(final PaginationRequest request) {
        this(request.getPageNumber(), request.getPageSize());
    }
}
