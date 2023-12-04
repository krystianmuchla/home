package com.example.skyr.pagination;

public record Pagination(int pageNumber, int pageSize) {
    public static final int MIN_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 1000;
    public static final int DEFAULT_PAGE_SIZE = 20;

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
}
