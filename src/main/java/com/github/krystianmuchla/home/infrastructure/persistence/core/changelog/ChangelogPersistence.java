package com.github.krystianmuchla.home.infrastructure.persistence.core.changelog;

import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class ChangelogPersistence extends Persistence {
    private static final String CHANGELOG = "changelog";

    public static boolean exists() {
        var result = executeQuery("PRAGMA table_list", tableName());
        return result.contains(CHANGELOG);
    }

    public static void create() {
        executeUpdate(
            "CREATE TABLE %s (%s INT PRIMARY KEY, %s TIMESTAMP(3) NOT NULL)".formatted(
                CHANGELOG,
                Change.ID,
                Change.EXECUTION_TIME
            )
        );
    }

    public static void createChange(Change... changes) {
        for (var change : changes) {
            var sql = new Sql.Builder()
                .insertInto(CHANGELOG)
                .values(
                    change.id(),
                    change.executionTime()
                );
            executeUpdate(sql.build());
        }
    }

    public static Change getLastChange() {
        var sql = new Sql.Builder()
            .select()
            .from(CHANGELOG)
            .orderBy(Change.ID)
            .desc()
            .limit(1);
        var result = executeQuery(sql.build(), Change::fromResultSet);
        return singleResult(result);
    }

    private static Function<ResultSet, String> tableName() {
        return resultSet -> {
            try {
                return resultSet.getString("name");
            } catch (SQLException exception) {
                throw new RuntimeException(exception);
            }
        };
    }
}
