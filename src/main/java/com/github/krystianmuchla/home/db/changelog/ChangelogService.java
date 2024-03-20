package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.util.FileAccessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class ChangelogService {
    public static void update() {
        if (!ChangelogSql.hasChangelog()) {
            Transaction.run(ChangelogSql::createChangelog);
        }
        final var lastChangeId = ChangelogSql.getLastChangeId();
        int changeId;
        if (lastChangeId == null) {
            changeId = 1;
        } else {
            changeId = lastChangeId + 1;
        }
        File file;
        while ((file = FileAccessor.getFromResources("db/changelog/" + changeId + ".sql")) != null) {
            final List<String> statements = FileAccessor.readFile(file, ";");
            if (statements.getLast().isBlank()) {
                statements.removeLast();
            }
            final int finalChangeId = changeId;
            Transaction.run(() -> {
                statements.forEach(statement -> Sql.executeUpdate(statement.trim()));
                ChangelogSql.addToChangelog(finalChangeId);
            });
            log.info("Executed database change with id: {}", changeId);
            changeId++;
        }
    }
}
