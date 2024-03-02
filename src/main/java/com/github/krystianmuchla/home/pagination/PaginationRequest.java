package com.github.krystianmuchla.home.pagination;

import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

@Getter
public class PaginationRequest {
    private final int pageNumber;
    private final int pageSize;

    public PaginationRequest(final HttpServletRequest request) {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        final var pageNumber = request.getParameter("pageNumber");
        if (pageNumber == null) {
            this.pageNumber = 1;
        } else {
            this.pageNumber = Integer.parseInt(pageNumber);
            if (this.pageNumber < Pagination.MIN_PAGE_NUMBER) {
                errors.add("pageNumber", ValidationError.belowValue(Pagination.MIN_PAGE_NUMBER));
            }
        }
        final var pageSize = request.getParameter("pageSize");
        if (pageSize == null) {
            this.pageSize = 20;
        } else {
            this.pageSize = Integer.parseInt(pageSize);
            if (this.pageSize < Pagination.MIN_PAGE_SIZE) {
                errors.add("pageSize", ValidationError.belowValue(Pagination.MIN_PAGE_SIZE));
            }
            if (this.pageSize > Pagination.MAX_PAGE_SIZE) {
                errors.add("pageSize", ValidationError.aboveValue(Pagination.MAX_PAGE_SIZE));
            }
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
