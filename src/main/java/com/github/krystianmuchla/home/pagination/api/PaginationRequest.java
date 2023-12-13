package com.github.krystianmuchla.home.pagination.api;

import com.github.krystianmuchla.home.pagination.Pagination;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public final class PaginationRequest {
    @NotNull @Size(min = Pagination.MIN_PAGE_NUMBER) Integer pageNumber = Pagination.DEFAULT_PAGE_NUMBER;
    @NotNull @Size(min = Pagination.MIN_PAGE_SIZE, max = Pagination.MAX_PAGE_SIZE) Integer pageSize = Pagination.DEFAULT_PAGE_SIZE;
}
