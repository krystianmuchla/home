package com.github.krystianmuchla.home.drive.directory;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.and;
import static com.github.krystianmuchla.home.db.Sql.eq;

public class DirectoryPersistence extends Persistence {
    public static void create(Directory directory) {
        var sql = new Sql.Builder()
            .insertInto(Directory.TABLE)
            .values(
                directory.id,
                directory.userId,
                directory.status,
                directory.parentId,
                directory.name,
                directory.creationTime,
                directory.modificationTime
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
}
