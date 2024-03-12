package com.github.krystianmuchla.home.id.user;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Function;

import com.github.krystianmuchla.home.Dao;

import lombok.SneakyThrows;

public class UserDao extends Dao {
    public static final UserDao INSTANCE = new UserDao();

    public void create(final User user) {
        executeUpdate("INSERT INTO user VALUES (?)", user.id().toString());
    }

    public User readById(final UUID id) {
        final var result = executeQuery("SELECT * FROM user WHERE id = ?", mapper(), id.toString());
        return singleResult(result);
    }

    private Function<ResultSet, User> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public User apply(final ResultSet resultSet) {
                return new User(resultSet);
            }
        };
    }
}
