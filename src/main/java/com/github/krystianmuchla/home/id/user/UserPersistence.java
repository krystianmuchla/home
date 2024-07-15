package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.util.UUID;

public class UserPersistence extends Persistence {
    public static void create(final User user) {
        final var sql = new Sql.Builder()
            .insertInto(User.TABLE)
            .values(
                user.id(),
                user.name()
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
        final var result = executeQuery(sql.build(), User::fromResultSet);
        return singleResult(result);
    }
}
