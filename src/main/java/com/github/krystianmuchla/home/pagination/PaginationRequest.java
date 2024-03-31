package com.github.krystianmuchla.home.pagination;

import com.github.krystianmuchla.home.api.RequestReader;
import com.github.krystianmuchla.home.error.exception.validation.ValidationError;
import com.github.krystianmuchla.home.error.exception.validation.ValidationException;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import jakarta.servlet.http.HttpServletRequest;

public record PaginationRequest(int pageNumber, int pageSize) {
    public static PaginationRequest from(final HttpServletRequest request) {
        final var errors = new MultiValueHashMap<String, ValidationError>();
        final var pageNumber = RequestReader.readQueryParameter(request, "pageNumber", Integer::valueOf, 1);
        if (pageNumber < Pagination.MIN_PAGE_NUMBER) {
            errors.add("pageNumber", ValidationError.belowValue(Pagination.MIN_PAGE_NUMBER));
        }
        final var pageSize = RequestReader.readQueryParameter(request, "pageSize", Integer::valueOf, 20);
        if (pageSize < Pagination.MIN_PAGE_SIZE) {
            errors.add("pageSize", ValidationError.belowValue(Pagination.MIN_PAGE_SIZE));
        }
        if (pageSize > Pagination.MAX_PAGE_SIZE) {
            errors.add("pageSize", ValidationError.aboveValue(Pagination.MAX_PAGE_SIZE));
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return new PaginationRequest(pageNumber, pageSize);
    }
}
