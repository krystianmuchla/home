package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public class UserPersistence extends Persistence {
    public static void create(final User user) {
        final var sql = new Sql.Builder()
            .insertInto(User.TABLE)
            .values(
                user.id()
            );
        executeUpdate(sql.build());
    }

    public static User read(final UUID id) {
        final var sql = new Sql.Builder()
            .select()
            .from(User.TABLE)
            .where(
                Sql.eq(User.ID, id)
            );
        final var result = executeQuery(sql.build(), mapper());
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
