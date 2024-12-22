package com.github.krystianmuchla.home.infrastructure.persistence.id;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.util.UUID;

public class UserPersistence extends Persistence {
    public static void create(User user) {
        var creationTime = new Time();
        var sql = new Sql.Builder()
            .insertInto(User.TABLE)
            .values(
                user.id,
                user.name,
                creationTime,
                creationTime,
                1
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
