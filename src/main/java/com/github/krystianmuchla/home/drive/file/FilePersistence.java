package com.github.krystianmuchla.home.drive.file;

import com.github.krystianmuchla.home.db.Persistence;
import com.github.krystianmuchla.home.db.Sql;

import java.util.List;
import java.util.UUID;

import static com.github.krystianmuchla.home.db.Sql.and;
import static com.github.krystianmuchla.home.db.Sql.eq;

public class FilePersistence extends Persistence {
    public static void create(final File file) {
        final var sql = new Sql.Builder()
            .insertInto(File.TABLE)
            .values(
                file.id(),
                file.userId(),
                file.directoryId(),
                file.path()
            );
        executeUpdate(sql.build());
    }

    public static File readById(final UUID userId, final UUID id) {
        final var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(
                eq(File.ID, id),
                and(),
                eq(File.USER_ID, userId)
            );
        final var result = executeQuery(sql.build(), File::fromResultSet);
        return singleResult(result);
    }

    public static List<File> readByDirectoryId(final UUID userId, final UUID directoryId) {
        final var sql = new Sql.Builder()
            .select()
            .from(File.TABLE)
            .where(eq(File.USER_ID, userId))
            .and();
        if (directoryId == null) {
            sql.isNull(File.DIRECTORY_ID);
        } else {
            sql.eq(File.DIRECTORY_ID, directoryId);
        }
        return executeQuery(sql.build(), File::fromResultSet);
    }
}
