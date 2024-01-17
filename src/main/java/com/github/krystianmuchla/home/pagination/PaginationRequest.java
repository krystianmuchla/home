package com.github.krystianmuchla.home.pagination;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class PaginationRequest {
    private final int pageNumber;
    private final int pageSize;

    public PaginationRequest(final HttpServletRequest request) {
        final var pageNumber = request.getParameter("pageNumber");
        if (pageNumber == null) {
            this.pageNumber = 1;
        } else {
            this.pageNumber = Integer.parseInt(pageNumber);
            if (this.pageNumber < Pagination.MIN_PAGE_NUMBER) throw new IllegalArgumentException();
        }
        final var pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            this.pageSize = 20;
        } else {
            this.pageSize = Integer.parseInt(pageSize);
            if (this.pageSize < Pagination.MIN_PAGE_SIZE) throw new IllegalArgumentException();
            if (this.pageSize > Pagination.MAX_PAGE_SIZE) throw new IllegalArgumentException();
        }
    }
}
