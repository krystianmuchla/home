package com.github.krystianmuchla.home.pagination;

import java.util.List;

public record PaginatedResult<T>(List<T> data, PaginationResult pagination) {
}
