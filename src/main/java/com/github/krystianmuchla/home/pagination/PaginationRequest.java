package com.github.krystianmuchla.home.pagination;

import com.github.krystianmuchla.home.api.RequestQuery;
import com.github.krystianmuchla.home.exception.ValidationError;
import com.github.krystianmuchla.home.exception.http.BadRequestException;
import com.github.krystianmuchla.home.util.MultiValueHashMap;
import com.github.krystianmuchla.home.util.MultiValueMap;

public record PaginationRequest(int pageNumber, int pageSize) implements RequestQuery {
    public PaginationRequest(MultiValueMap<String, String> query) {
        this(resolvePageNumber(query), resolvePageSize(query));
    }


    @Override
    public void validate() {
        var errors = new MultiValueHashMap<String, ValidationError>();
        if (pageNumber < Pagination.MIN_PAGE_NUMBER) {
            errors.add("pageNumber", ValidationError.belowValue(Pagination.MIN_PAGE_NUMBER));
        }
        if (pageSize < Pagination.MIN_PAGE_SIZE) {
            errors.add("pageSize", ValidationError.belowValue(Pagination.MIN_PAGE_SIZE));
        }
        if (pageSize > Pagination.MAX_PAGE_SIZE) {
            errors.add("pageSize", ValidationError.aboveValue(Pagination.MAX_PAGE_SIZE));
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }

    private static int resolvePageNumber(MultiValueMap<String, String> query) {
        var pageNumber = query.getFirst("pageNumber");
        return pageNumber.map(Integer::parseInt).orElse(1);
    }

    private static int resolvePageSize(MultiValueMap<String, String> query) {
        var pageSize = query.getFirst("pageSize");
        return pageSize.map(Integer::parseInt).orElse(20);
    }
}
