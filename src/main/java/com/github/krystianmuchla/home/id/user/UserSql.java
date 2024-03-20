package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Sql;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.function.Function;

public class UserSql extends Sql {
    public static void create(final User user) {
        executeUpdate("INSERT INTO user VALUES (?)", user.id().toString());
    }

    public static User readById(final UUID id) {
        final var result = executeQuery("SELECT * FROM user WHERE id = ?", mapper(), id.toString());
        return singleResult(result);
    }

    public static void delete() {
        executeUpdate("DELETE FROM user");
    }

    private static Function<ResultSet, User> mapper() {
        return new Function<>() {
            @Override
            @SneakyThrows
            public User apply(final ResultSet resultSet) {
                return new User(resultSet);
            }
        };
    }
}
