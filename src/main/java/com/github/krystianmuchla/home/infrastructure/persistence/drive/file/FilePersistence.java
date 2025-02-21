package com.github.krystianmuchla.home.infrastructure.persistence.drive.file;

import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.drive.file.File;
import com.github.krystianmuchla.home.domain.drive.file.FileStatus;
import com.github.krystianmuchla.home.domain.drive.file.error.FileValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.and;
import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;
import static com.github.krystianmuchla.home.infrastructure.persistence.drive.file.FileColumn.*;

public class FilePersistence extends Persistence {
    private static final String TABLE = "file";
    private static final Function<File.Field, String> COLUMNS;

    static {
        COLUMNS = field -> switch (field) {
            case ID -> ID;
            case USER_ID -> USER_ID;
            case STATUS -> STATUS;
            case DIRECTORY_ID -> DIRECTORY_ID;
            case NAME -> NAME;
            case CREATION_TIME -> CREATION_TIME;
            case MODIFICATION_TIME -> MODIFICATION_TIME;
            case VERSION -> VERSION;
        };
    }

    public static void create(File file) {
        var sql = new Sql.Builder()
            .insertInto(TABLE)
            .values(
                file.id,
                file.userId,
                file.status,
                file.directoryId,
                file.name,
                file.creationTime,
                file.modificationTime,
                file.version
            );
        executeUpdate(sql.build());
    }

    public static File readByIdAndStatus(UUID userId, UUID id, FileStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(ID, id),
                and(),
                eq(USER_ID, userId),
                and(),
                eq(STATUS, status)
            );
        var result = executeQuery(sql.build(), FilePersistence::map);
        return singleResult(result);
    }

    public static List<File> readByDirectoryId(UUID userId, UUID directoryId) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId)
            )
            .and();
        if (directoryId == null) {
            sql.isNull(DIRECTORY_ID);
        } else {
            sql.eq(DIRECTORY_ID, directoryId);
        }
        return executeQuery(sql.build(), FilePersistence::map);
    }

    public static List<File> readByDirectoryIdAndStatus(UUID userId, UUID directoryId, FileStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId),
                and(),
                eq(STATUS, status)
            )
            .and();
        if (directoryId == null) {
            sql.isNull(DIRECTORY_ID);
        } else {
            sql.eq(DIRECTORY_ID, directoryId);
        }
        return executeQuery(sql.build(), FilePersistence::map);
    }

    public static List<File> readByStatus(FileStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(STATUS, status)
            );
        return executeQuery(sql.build(), FilePersistence::map);
    }

    public static boolean update(File file) {
        var updates = file.consumeUpdates();
        var sql = new Sql.Builder()
            .update(TABLE)
            .set(toSql(updates, COLUMNS))
            .where(
                eq(ID, file.id),
                and(),
                eq(USER_ID, file.userId),
                and(),
                eq(VERSION, file.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(File file) {
        var sql = new Sql.Builder()
            .delete()
            .from(TABLE)
            .where(
                eq(ID, file.id),
                and(),
                eq(USER_ID, file.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    private static File map(ResultSet resultSet) {
        try {
            return new File(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                FileStatus.valueOf(resultSet.getString(STATUS)),
                UUIDFactory.create(resultSet.getString(DIRECTORY_ID)),
                resultSet.getString(NAME),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | FileValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
