package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class UserSql extends Sql {
    public static void create(final User user) {
        executeUpdate(
            "INSERT INTO %s VALUES (?)".formatted(User.USER),
            user.id().toString()
        );
    }

    public static User read(final UUID id) {
        final var result = executeQuery(
            "SELECT * FROM %s WHERE %s = ?".formatted(User.USER, User.ID),
            mapper(),
            id.toString()
        );
        return singleResult(result);
    }

    public static Function<ResultSet, User> mapper() {
        return resultSet -> {
            try {
                return new User(resultSet);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }
}
