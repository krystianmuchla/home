package com.github.krystianmuchla.home.id.accessdata;

import com.github.krystianmuchla.home.db.Sql;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.function.Function;

public class AccessDataSql extends Sql {
    public static void create(final AccessData accessData) {
        executeUpdate(
            "INSERT INTO access_data VALUES (?, ?, ?, ?, ?)",
            accessData.id().toString(),
            accessData.userId().toString(),
            accessData.login(),
            accessData.salt(),
            accessData.secret()
        );
    }

    public static AccessData readByLogin(final String login) {
        final var result = executeQuery("SELECT * FROM access_data WHERE login = ?", mapper(), login);
        return singleResult(result);
    }

    public static void delete() {
        executeUpdate("DELETE FROM access_data");
    }

    private static Function<ResultSet, AccessData> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public AccessData apply(final ResultSet resultSet) {
                return new AccessData(resultSet);
            }
        };
    }
}
