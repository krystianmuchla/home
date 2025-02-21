package com.github.krystianmuchla.home.infrastructure.persistence.id.accessdata;

import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessData;
import com.github.krystianmuchla.home.domain.id.accessdata.error.AccessDataValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;
import static com.github.krystianmuchla.home.infrastructure.persistence.id.accessdata.AccessDataColumn.*;

public class AccessDataPersistence extends Persistence {
    private static final String TABLE = "access_data";

    public static void create(AccessData accessData) {
        var sql = new Sql.Builder()
            .insertInto(TABLE)
            .values(
                accessData.id,
                accessData.userId,
                accessData.login,
                accessData.salt,
                accessData.secret,
                accessData.creationTime,
                accessData.modificationTime,
                accessData.version
            );
        executeUpdate(sql.build());
    }

    public static AccessData read(String login) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(LOGIN, login)
            );
        var result = executeQuery(sql.build(), AccessDataPersistence::map);
        return singleResult(result);
    }

    private static AccessData map(ResultSet resultSet) {
        try {
            return new AccessData(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                resultSet.getString(LOGIN),
                resultSet.getBytes(SALT),
                resultSet.getBytes(SECRET),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | AccessDataValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
