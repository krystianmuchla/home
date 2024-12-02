package com.github.krystianmuchla.home.infrastructure.persistence.id;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.domain.id.AccessData;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;

public class AccessDataPersistence extends Persistence {
    private static final Map<String, AccessData> READ_CACHE = new ConcurrentHashMap<>();

    public static void create(AccessData accessData) {
        var creationTime = InstantFactory.create();
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
        var cachedAccessData = READ_CACHE.get(login);
        if (cachedAccessData != null) {
            return cachedAccessData;
        }
        var sql = new Sql.Builder()
            .select()
            .from(AccessData.TABLE)
            .where(
                eq(AccessData.LOGIN, login)
            );
        var result = executeQuery(sql.build(), AccessData::fromResultSet);
        var accessData = singleResult(result);
        if (accessData != null) {
            READ_CACHE.put(login, accessData);
        }
        return accessData;
    }
}
