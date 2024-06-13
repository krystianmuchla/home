package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static com.github.krystianmuchla.home.db.Sql.eq;

public class AccessDataPersistence extends Persistence {
    private static final Map<String, AccessData> READ_CACHE = new ConcurrentHashMap<>();

    public static void create(final AccessData accessData) {
        final var sql = new Sql.Builder()
            .insertInto(AccessData.TABLE)
            .values(
                accessData.id(),
                accessData.userId(),
                accessData.login(),
                accessData.salt(),
                accessData.secret()
            );
        executeUpdate(sql.build());
    }

    public static AccessData read(final String login) {
        final var cachedAccessData = READ_CACHE.get(login);
        if (cachedAccessData != null) {
            return cachedAccessData;
        }
        final var sql = new Sql.Builder()
            .select()
            .from(AccessData.TABLE)
            .where(
                eq(AccessData.LOGIN, login)
            );
        final var result = executeQuery(sql.build(), mapper());
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
