package com.github.krystianmuchla.home.infrastructure.persistence.drive;

import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.and;
import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;

public class DirectoryPersistence extends Persistence {
    public static void create(Directory directory) {
        var creationTime = InstantFactory.create();
        var sql = new Sql.Builder()
            .insertInto(Directory.TABLE)
            .values(
                directory.id,
                directory.userId,
                directory.status,
                directory.parentId,
                directory.name,
                creationTime,
                creationTime,
                1
            );
        executeUpdate(sql.build());
    }

    public static Directory readByIdAndStatus(UUID userId, UUID id, DirectoryStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(Directory.TABLE)
            .where(
                eq(Directory.ID, id),
                and(),
                eq(Directory.USER_ID, userId),
                and(),
                eq(Directory.STATUS, status)
            );
        var result = executeQuery(sql.build(), Directory::fromResultSet);
        return singleResult(result);
    }

    public static List<Directory> readByParentId(UUID userId, UUID parentId) {
        var sql = new Sql.Builder()
            .select()
            .from(Directory.TABLE)
            .where(
                eq(Directory.USER_ID, userId)
            )
            .and();
        if (parentId == null) {
            sql.isNull(Directory.PARENT_ID);
        } else {
            sql.eq(Directory.PARENT_ID, parentId);
        }
        return executeQuery(sql.build(), Directory::fromResultSet);
    }

    public static List<Directory> readByParentIdAndStatus(UUID userId, UUID parentId, DirectoryStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(Directory.TABLE)
            .where(
                eq(Directory.USER_ID, userId),
                and(),
                eq(Directory.STATUS, status)
            )
            .and();
        if (parentId == null) {
            sql.isNull(Directory.PARENT_ID);
        } else {
            sql.eq(Directory.PARENT_ID, parentId);
        }
        return executeQuery(sql.build(), Directory::fromResultSet);
    }

    public static List<Directory> readByStatus(DirectoryStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(Directory.TABLE)
            .where(
                eq(Directory.STATUS, status)
            );
        return executeQuery(sql.build(), Directory::fromResultSet);
    }

    public static boolean update(Directory directory) {
        var updates = directory.consumeUpdates();
        updates.put(Directory.MODIFICATION_TIME, InstantFactory.create());
        updates.put(Directory.VERSION, directory.version + 1);
        var sql = new Sql.Builder()
            .update(Directory.TABLE)
            .set(toSql(updates))
            .where(
                eq(Directory.ID, directory.id),
                and(),
                eq(Directory.USER_ID, directory.userId),
                and(),
                eq(Directory.VERSION, directory.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(Directory directory) {
        var sql = new Sql.Builder()
            .delete()
            .from(Directory.TABLE)
            .where(
                eq(Directory.ID, directory.id),
                and(),
                eq(Directory.USER_ID, directory.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }
}
