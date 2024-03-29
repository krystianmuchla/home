package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationResult;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Sql {
    @SneakyThrows
    public static <T> List<T> executeQuery(
        final String sql,
        final Function<ResultSet, T> mapper,
        final Object... parameters
    ) {
        final var connection = ConnectionManager.getConnection();
        try (final var preparedStatement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.length; index++) {
                preparedStatement.setObject(index + 1, parameters[index]);
            }
            final var resultSet = preparedStatement.executeQuery();
            final var result = new ArrayList<T>();
            while (resultSet.next()) {
                result.add(mapper.apply(resultSet));
            }
            return result;
        }
    }

    @SneakyThrows
    public static int executeUpdate(final String sql, final Object... parameters) {
        final var connection = ConnectionManager.getConnection();
        try (final var preparedStatement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.length; index++) {
                preparedStatement.setObject(index + 1, parameters[index]);
            }
            return preparedStatement.executeUpdate();
        }
    }

    protected static boolean boolResult(final int result) {
        return result > 0;
    }

    protected static <T> T singleResult(final List<T> result) {
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() == 1) {
            return result.getFirst();
        }
        throw new InternalException("Could not resolve single result");
    }

    protected static <T> PaginatedResult<T> paginatedResult(final Pagination pagination, final List<T> result) {
        final var fetchedElements = result.size();
        final var next = fetchedElements > pagination.pageSize();
        if (next) {
            result.removeLast();
        }
        final var paginationResult = new PaginationResult(next, pagination.pageNumber() > 1);
        return new PaginatedResult<>(result, paginationResult);
    }

    protected static int limit(final int pageSize) {
        return pageSize + 1;
    }

    protected static int offset(final int pageNumber, final int pageSize) {
        return (pageNumber - 1) * pageSize;
    }

    protected static Timestamp timestamp(final Instant instant) {
        return Timestamp.valueOf(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    protected static String setters(final LinkedHashMap<String, String> parameters) {
        return parameters.keySet().stream().map(key -> key + " = ?").collect(Collectors.joining(", "));
    }
}
