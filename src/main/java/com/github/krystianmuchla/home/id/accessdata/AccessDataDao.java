package com.github.krystianmuchla.home.id.accessdata;

import java.sql.ResultSet;
import java.util.function.Function;

import com.github.krystianmuchla.home.Dao;

import lombok.SneakyThrows;

public class AccessDataDao extends Dao {
    public static final AccessDataDao INSTANCE = new AccessDataDao();

    public void create(final AccessData accessData) {
        executeUpdate("INSERT INTO access_data VALUES (?, ?, ?, ?, ?)",
                accessData.id().toString(),
                accessData.user_id().toString(),
                accessData.login(),
                accessData.salt(),
                accessData.secret());
    }

    public AccessData readByLogin(final String login) {
        final var result = executeQuery("SELECT * FROM access_data WHERE login = ?",
                mapper(),
                login);
        return singleResult(result);
    }

    private Function<ResultSet, AccessData> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public AccessData apply(final ResultSet resultSet) {
                return new AccessData(resultSet);
            }
        };
    }
}
