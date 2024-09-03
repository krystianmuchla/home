package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.util.UUID;

public class UserPersistence extends Persistence {
    public static void create(User user) {
        var sql = new Sql.Builder()
            .insertInto(User.TABLE)
            .values(
                user.id(),
                user.name(),
                user.creationTime(),
                user.modificationTime()
            );
        executeUpdate(sql.build());
    }

    public static User read(UUID id) {
        var sql = new Sql.Builder()
            .select()
            .from(User.TABLE)
            .where(
                Sql.eq(User.ID, id)
            );
        var result = executeQuery(sql.build(), User::fromResultSet);
        return singleResult(result);
    }
}
