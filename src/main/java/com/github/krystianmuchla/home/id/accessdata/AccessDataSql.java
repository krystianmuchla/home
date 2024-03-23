package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.error.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class AccessDataSql extends Sql {
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

    public static AccessData readByLogin(final String login) {
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ?".formatted(AccessData.ACCESS_DATA, AccessData.LOGIN),
            mapper(),
            login
        );
        return singleResult(result);
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
