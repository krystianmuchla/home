package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.InstantFactory;
import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.util.FileAccessor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class ChangelogService {
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
        while ((file = FileAccessor.getFromResources("db/changelog/" + changeId + ".sql")) != null) {
            final List<String> statements = FileAccessor.readFile(file, ";");
            if (statements.getLast().isBlank()) {
                statements.removeLast();
            }
            final int finalChangeId = changeId;
            Transaction.run(() -> {
                statements.forEach(statement -> Sql.executeUpdate(statement.trim()));
                ChangelogSql.createChange(new Change(finalChangeId, InstantFactory.create()));
            });
            log.info("Executed database change with id: {}", changeId);
            changeId++;
        }
    }
}
