package com.github.krystianmuchla.home.infrastructure.persistence.core;

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
        try (var transaction = TransactionManager.createReadTransaction()) {
            try (var preparedStatement = transaction.connection.prepareStatement(sql)) {
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
            throw new RuntimeException(exception);
        }
    }

    public static int executeUpdate(Sql sql) {
        return executeUpdate(sql.template(), sql.parameters());
    }

    public static int executeUpdate(String sql, Object... parameters) {
        var transaction = TransactionManager.getTransaction();
        try (var preparedStatement = transaction.connection.prepareStatement(sql)) {
            for (int index = 0; index < parameters.length; index++) {
                preparedStatement.setObject(index + 1, parameters[index]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
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
        throw new IllegalStateException("Could not resolve single result");
    }

    protected static <K> Sql[] toSql(Map<K, Object> updates, Function<K, String> mapper) {
        return updates.entrySet()
            .stream()
            .map(entry -> {
                var field = mapper.apply(entry.getKey());
                var value = entry.getValue();
                return Sql.eq(field, value);
            })
            .toArray(Sql[]::new);
    }
}
