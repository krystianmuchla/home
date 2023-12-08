package com.github.krystianmuchla.skyr;

import com.github.krystianmuchla.skyr.exception.ServerErrorException;
import com.github.krystianmuchla.skyr.pagination.PaginationResult;
import com.github.krystianmuchla.skyr.pagination.PaginatedResult;
import com.github.krystianmuchla.skyr.pagination.Pagination;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public abstract class Dao {
    protected boolean isUpdated(final int updatedRows) {
        return updatedRows > 0;
    }

    protected <T> T singleResult(final List<T> result) {
        if (result.isEmpty()) return null;
        if (result.size() == 1) return result.getFirst();
        throw new ServerErrorException("Could not resolve single result");
    }

    protected <T> PaginatedResult<T> paginatedResult(final Pagination pagination, final List<T> result) {
        final var fetchedElements = result.size();
        final var next = fetchedElements > pagination.pageSize();
        if (next) result.removeLast();
        final var paginationResult = new PaginationResult(next, pagination.pageNumber() > 1);
        return new PaginatedResult<>(result, paginationResult);
    }

    protected int limit(final int pageSize) {
        return pageSize + 1;
    }

    protected int offset(final int pageNumber, final int pageSize) {
        return (pageNumber - 1) * pageSize;
    }

    protected Timestamp timestamp(final Instant instant) {
        if (instant == null) return null;
        return Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }
}
