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
                file.getId(),
                file.getUserId(),
                file.getStatus(),
                file.getDirectoryId(),
                file.getName(),
                file.getCreationTime(),
                file.getModificationTime()
            );
        executeUpdate(sql.build());
    }

    public static File read(UUID userId, UUID id) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.ID, id),
                and(),
                eq(File.USER_ID, userId)
            );
        var result = executeQuery(sql.build(), File::fromResultSet);
        return singleResult(result);
    }

    public static List<File> read(UUID userId, UUID directoryId, FileStatus status) {
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
        return executeQuery(sql.build(), File::fromResultSet);
    }

    public static File readForUpdate(UUID userId, UUID id) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.ID, id),
                and(),
                eq(File.USER_ID, userId)
            )
            .forUpdate();
        var result = executeQuery(sql.build(), File::fromResultSet);
        return singleResult(result);
    }

    public static List<File> readForUpdate(FileStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.STATUS, status)
            )
            .forUpdate();
        return executeQuery(sql.build(), File::fromResultSet);
    }

    public static boolean update(File file) {
        var sql = new Sql.Builder()
            .update(File.TABLE)
            .set(
                eq(File.STATUS, file.getStatus()),
                eq(File.MODIFICATION_TIME, file.getModificationTime())
            )
            .where(
                eq(File.ID, file.getId())
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }
}
