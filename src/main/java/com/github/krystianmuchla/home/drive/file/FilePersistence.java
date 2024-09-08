package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.and;
import static com.github.krystianmuchla.home.db.Sql.eq;

public class FilePersistence extends Persistence {
    public static void create(File file) {
        var sql = new Sql.Builder()
            .insertInto(File.TABLE)
            .values(
                file.id,
                file.userId,
                file.status,
                file.directoryId,
                file.name,
                file.creationTime,
                file.modificationTime
            );
        executeUpdate(sql.build());
    }

    public static File readByIdAndStatus(UUID userId, UUID id, FileStatus status, boolean forUpdate) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.ID, id),
                and(),
                eq(File.USER_ID, userId),
                and(),
                eq(File.STATUS, status)
            );
        if (forUpdate) {
            sql.forUpdate();
        }
        var result = executeQuery(sql.build(), File::fromResultSet);
        return singleResult(result);
    }

    public static List<File> readByDirectoryId(UUID userId, UUID directoryId) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.USER_ID, userId)
            )
            .and();
        if (directoryId == null) {
            sql.isNull(File.DIRECTORY_ID);
        } else {
            sql.eq(File.DIRECTORY_ID, directoryId);
        }
        return executeQuery(sql.build(), File::fromResultSet);
    }

    public static List<File> readByDirectoryIdAndStatus(UUID userId, UUID directoryId, FileStatus status, boolean forUpdate) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.USER_ID, userId),
                and(),
                eq(File.STATUS, status)
            )
            .and();
        if (directoryId == null) {
            sql.isNull(File.DIRECTORY_ID);
        } else {
            sql.eq(File.DIRECTORY_ID, directoryId);
        }
        if (forUpdate) {
            sql.forUpdate();
        }
        return executeQuery(sql.build(), File::fromResultSet);
    }

    public static List<File> readByStatus(FileStatus status, boolean forUpdate) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.STATUS, status)
            );
        if (forUpdate) {
            sql.forUpdate();
        }
        return executeQuery(sql.build(), File::fromResultSet);
    }

    public static boolean update(File file) {
        var sql = new Sql.Builder()
            .update(File.TABLE)
            .set(
                eq(File.STATUS, file.status),
                eq(File.MODIFICATION_TIME, file.modificationTime)
            )
            .where(
                eq(File.ID, file.id)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(File file) {
        var sql = new Sql.Builder()
            .delete()
            .from(File.TABLE)
            .where(
                eq(File.ID, file.id)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }
}
