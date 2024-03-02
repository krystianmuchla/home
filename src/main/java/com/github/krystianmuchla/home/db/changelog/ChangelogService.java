package com.github.krystianmuchla.home.db.changelog;

import java.io.File;
import java.util.List;

import com.github.krystianmuchla.home.FileAccessor;
import com.github.krystianmuchla.home.db.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChangelogService {
    public static final ChangelogService INSTANCE = new ChangelogService();

    private final ChangelogDao changelogDao = ChangelogDao.INSTANCE;

    public void update() {
        if (!changelogDao.hasChangelog()) {
            Transaction.run(changelogDao::createChangelog);
        }
        final var lastChangeId = changelogDao.getLastChangeId();
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
                statements.forEach(statement -> changelogDao.executeUpdate(statement.trim()));
                changelogDao.addToChangelog(finalChangeId);
            });
            log.info("Executed database change with id: {}", changeId);
            changeId++;
        }
    }
}
