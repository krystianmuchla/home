package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.exception.InternalException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

public class ChangelogSql extends Sql {
    private static final String CHANGELOG = "changelog";

    public static boolean exists() {
        final var result = executeQuery("SHOW TABLES LIKE ?", stringMapper(), CHANGELOG);
        return boolResult(result.size());
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

    public static void createChange(final Change... changes) {
        for (final var change : changes) {
            executeUpdate(
                "INSERT INTO %s VALUES (?, ?)".formatted(CHANGELOG),
                change.id(),
                timestamp(change.executionTime()).toString()
            );
        }
    }

    public static Change getLastChange() {
        final var result = executeQuery(
            "SELECT * FROM %s ORDER BY %s DESC LIMIT 1".formatted(CHANGELOG, Change.ID),
            mapper()
        );
        return singleResult(result);
    }

    public static Function<ResultSet, Change> mapper() {
        return resultSet -> {
            try {
                return new Change(resultSet);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }

    public static Function<ResultSet, String> stringMapper() {
        return resultSet -> {
            try {
                return resultSet.getString(1);
            } catch (final SQLException exception) {
                throw new InternalException(exception);
            }
        };
    }
}
