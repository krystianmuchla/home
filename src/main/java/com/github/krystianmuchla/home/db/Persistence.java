package com.github.krystianmuchla.home.db;

import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Persistence {
    public static <T> List<T> executeQuery(Sql sql, Function<ResultSet, T> mapper) {
        return executeQuery(sql.template(), mapper, sql.parameters());
    }

    public static <T> List<T> executeQuery(String sql, Function<ResultSet, T> mapper, Object... parameters) {
        try (var readConnection = ConnectionManager.getReadConnection()) {
            var connection = readConnection.connection();
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
        var connection = ConnectionManager.getWriteConnection();
        try (var preparedStatement = connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.length; index++) {
                preparedStatement.setObject(index + 1, parameters[index]);
            }
            return preparedStatement.executeUpdate();
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

    protected static Sql[] toSql(Map<String, Object> updates) {
        return updates.entrySet().stream().map(entry -> Sql.eq(entry.getKey(), entry.getValue())).toArray(Sql[]::new);
    }
}
