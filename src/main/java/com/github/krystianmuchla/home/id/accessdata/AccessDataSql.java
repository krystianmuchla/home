package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class AccessDataSql extends Sql {
    private static final Map<String, AccessData> READ_CACHE = new ConcurrentHashMap<>();

    public static void create(final AccessData accessData) {
        executeUpdate(
            "INSERT INTO %s VALUES (?, ?, ?, ?, ?)".formatted(AccessData.ACCESS_DATA),
            accessData.id().toString(),
            accessData.userId().toString(),
            accessData.login(),
            accessData.salt(),
            accessData.secret()
        );
    }

    public static AccessData read(final String login) {
        final var cachedAccessData = READ_CACHE.get(login);
        if (cachedAccessData != null) {
            return cachedAccessData;
        }
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ?".formatted(AccessData.ACCESS_DATA, AccessData.LOGIN),
            mapper(),
            login
        );
        final var accessData = singleResult(result);
        if (accessData != null) {
            READ_CACHE.put(login, accessData);
        }
        return accessData;
    }

    public static Function<ResultSet, AccessData> mapper() {
        return resultSet -> {
            try {
                return new AccessData(resultSet);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }
}
