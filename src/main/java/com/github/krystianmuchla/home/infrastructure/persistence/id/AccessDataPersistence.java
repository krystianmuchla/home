package com.github.krystianmuchla.home.infrastructure.persistence.id;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.domain.id.accessdata.AccessData;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;

public class AccessDataPersistence extends Persistence {
    public static void create(AccessData accessData) {
        var creationTime = new Time();
        var sql = new Sql.Builder()
            .insertInto(AccessData.TABLE)
            .values(
                accessData.id,
                accessData.userId,
                accessData.login,
                accessData.salt,
                accessData.secret,
                creationTime,
                creationTime,
                1
            );
        executeUpdate(sql.build());
    }

    public static AccessData read(String login) {
        var sql = new Sql.Builder()
            .select()
            .from(AccessData.TABLE)
            .where(
                eq(AccessData.LOGIN, login)
            );
        var result = executeQuery(sql.build(), AccessData::fromResultSet);
        return singleResult(result);
    }
}
