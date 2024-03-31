package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.error.exception.InternalException;
import com.github.krystianmuchla.home.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ChangelogService {
    private static final Logger LOG = LoggerFactory.getLogger(ChangelogService.class);

    public static void update() {
        if (!ChangelogSql.exists()) {
            Transaction.run(ChangelogSql::create);
        }
        final var lastChange = ChangelogSql.getLastChange();
        int changeId;
        if (lastChange == null) {
            changeId = 1;
        } else {
            changeId = lastChange.id() + 1;
        }
        File file;
        while ((file = FileManager.fromResources("db/changelog/" + changeId + ".sql")) != null) {
            final List<String> statements;
            try {
                statements = FileManager.read(file, ";");
            } catch (final FileNotFoundException exception) {
                throw new InternalException(exception);
            }
            if (statements.getLast().isBlank()) {
                statements.removeLast();
            }
            final int finalChangeId = changeId;
            Transaction.run(() -> {
                statements.forEach(statement -> Sql.executeUpdate(statement.trim()));
                ChangelogSql.createChange(new Change(finalChangeId, InstantFactory.create()));
            });
            LOG.info("Executed database change with id: {}", changeId);
            changeId++;
        }
    }
}
