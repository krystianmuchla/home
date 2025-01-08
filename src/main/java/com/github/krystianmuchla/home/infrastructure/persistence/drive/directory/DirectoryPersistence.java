package com.github.krystianmuchla.home.infrastructure.persistence.drive.directory;

import com.github.krystianmuchla.home.application.time.Time;
import com.github.krystianmuchla.home.application.time.TimeFactory;
import com.github.krystianmuchla.home.application.util.UUIDFactory;
import com.github.krystianmuchla.home.domain.drive.directory.Directory;
import com.github.krystianmuchla.home.domain.drive.directory.DirectoryStatus;
import com.github.krystianmuchla.home.domain.drive.directory.error.DirectoryValidationException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.and;
import static com.github.krystianmuchla.home.infrastructure.persistence.core.Sql.eq;
import static com.github.krystianmuchla.home.infrastructure.persistence.drive.directory.DirectoryColumn.*;

public class DirectoryPersistence extends Persistence {
    private static final String TABLE = "directory";
    private static final Function<Directory.Field, String> COLUMNS;

    static {
        COLUMNS = field -> switch (field) {
            case ID -> ID;
            case USER_ID -> USER_ID;
            case STATUS -> STATUS;
            case PARENT_ID -> PARENT_ID;
            case NAME -> NAME;
            case CREATION_TIME -> CREATION_TIME;
            case MODIFICATION_TIME -> MODIFICATION_TIME;
            case VERSION -> VERSION;
        };
    }

    public static void create(Directory directory) {
        var creationTime = new Time();
        var sql = new Sql.Builder()
            .insertInto(TABLE)
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
            .from(TABLE)
            .where(
                eq(ID, id),
                and(),
                eq(USER_ID, userId),
                and(),
                eq(STATUS, status)
            );
        var result = executeQuery(sql.build(), DirectoryPersistence::map);
        return singleResult(result);
    }

    public static List<Directory> readByParentId(UUID userId, UUID parentId) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId)
            )
            .and();
        if (parentId == null) {
            sql.isNull(PARENT_ID);
        } else {
            sql.eq(PARENT_ID, parentId);
        }
        return executeQuery(sql.build(), DirectoryPersistence::map);
    }

    public static List<Directory> readByParentIdAndStatus(UUID userId, UUID parentId, DirectoryStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(USER_ID, userId),
                and(),
                eq(STATUS, status)
            )
            .and();
        if (parentId == null) {
            sql.isNull(PARENT_ID);
        } else {
            sql.eq(PARENT_ID, parentId);
        }
        return executeQuery(sql.build(), DirectoryPersistence::map);
    }

    public static List<Directory> readByStatus(DirectoryStatus status) {
        var sql = new Sql.Builder()
            .select()
            .from(TABLE)
            .where(
                eq(STATUS, status)
            );
        return executeQuery(sql.build(), DirectoryPersistence::map);
    }

    public static boolean update(Directory directory) {
        var updates = directory.consumeUpdates();
        var sql = new Sql.Builder()
            .update(TABLE)
            .set(toSql(updates, COLUMNS))
            .where(
                eq(ID, directory.id),
                and(),
                eq(USER_ID, directory.userId),
                and(),
                eq(VERSION, directory.version)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    public static boolean delete(Directory directory) {
        var sql = new Sql.Builder()
            .delete()
            .from(TABLE)
            .where(
                eq(ID, directory.id),
                and(),
                eq(USER_ID, directory.userId)
            );
        var result = executeUpdate(sql.build());
        return boolResult(result);
    }

    private static Directory map(ResultSet resultSet) {
        try {
            return new Directory(
                UUIDFactory.create(resultSet.getString(ID)),
                UUIDFactory.create(resultSet.getString(USER_ID)),
                DirectoryStatus.valueOf(resultSet.getString(STATUS)),
                UUIDFactory.create(resultSet.getString(PARENT_ID)),
                resultSet.getString(NAME),
                TimeFactory.create(resultSet.getTimestamp(CREATION_TIME)),
                TimeFactory.create(resultSet.getTimestamp(MODIFICATION_TIME)),
                resultSet.getInt(VERSION)
            );
        } catch (SQLException | DirectoryValidationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
