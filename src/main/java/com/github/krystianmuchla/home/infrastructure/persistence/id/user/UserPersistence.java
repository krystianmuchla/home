package com.github.krystianmuchla.home.infrastructure.persistence.id.user;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.id.user.User;
import com.github.krystianmuchla.home.domain.id.user.error.UserValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static com.github.krystianmuchla.home.infrastructure.persistence.id.user.UserColumn.*;

public class UserPersistence extends Persistence {
    private static final String TABLE = "user";

    public static void create(User user) {
        var creationTime = new Time();
        var sql = new Sql.Builder()
            .insertInto(TABLE)
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
            .from(TABLE)
            .where(
                Sql.eq(ID, id)
            );
        var result = executeQuery(sql.build(), UserPersistence::map);
        return singleResult(result);
    }

    private static User map(ResultSet resultSet) {
        try {
            return new User(
                UUIDFactory.create(resultSet.getString(ID)),
                resultSet.getString(NAME),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | UserValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
