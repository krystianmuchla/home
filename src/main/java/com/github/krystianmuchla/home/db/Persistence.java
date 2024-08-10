package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.pagination.PaginatedResult;
import com.github.krystianmuchla.home.pagination.Pagination;
import com.github.krystianmuchla.home.pagination.PaginationResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Persistence {
    public static <T> List<T> executeQuery(Sql sql, Function<ResultSet, T> mapper) {
        return executeQuery(sql.template(), mapper, sql.parameters());
    }

    public static <T> List<T> executeQuery(String sql, Function<ResultSet, T> mapper, Object... parameters) {
        try (var borrowedConnection = ConnectionManager.borrowConnection()) {
            var connection = borrowedConnection.connection();
            try (var preparedStatement = connection.prepareStatement(sql)) {
                for (int index = 0; index < parameters.length; index++) {
                    preparedStatement.setObject(index + 1, parameters[index]);
                }
                try (var resultSet = preparedStatement.executeQuery()) {
                    var result = new ArrayList<T>();
                    while (resultSet.next()) {
                        result.add(mapper.apply(resultSet));
                    }
                    return result;
                }
            }
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }

    public static int executeUpdate(Sql sql) {
        return executeUpdate(sql.template(), sql.parameters());
    }

    public static int executeUpdate(String sql, Object... parameters) {
        try (var borrowedConnection = ConnectionManager.borrowConnection()) {
            var connection = borrowedConnection.connection();
            try (var preparedStatement = connection.prepareStatement(sql)) {
                for (int index = 0; index < parameters.length; index++) {
                    preparedStatement.setObject(index + 1, parameters[index]);
                }
                return preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new InternalException(exception);
        }
    }

    protected static boolean boolResult(int result) {
        return result > 0;
    }

    protected static <T> T singleResult(List<T> result) {
        if (result.isEmpty()) {
            return null;
        }
        if (result.size() == 1) {
            return result.getFirst();
        }
        throw new InternalException("Could not resolve single result");
    }

    protected static <T> PaginatedResult<T> paginatedResult(Pagination pagination, List<T> result) {
        var fetchedElements = result.size();
        var next = fetchedElements > pagination.pageSize();
        if (next) {
            result.removeLast();
        }
        var paginationResult = new PaginationResult(next, pagination.pageNumber() > 1);
        return new PaginatedResult<>(result, paginationResult);
    }

    protected static int limit(int pageSize) {
        return pageSize + 1;
    }

    protected static int offset(int pageNumber, int pageSize) {
        return (pageNumber - 1) * pageSize;
    }
}
