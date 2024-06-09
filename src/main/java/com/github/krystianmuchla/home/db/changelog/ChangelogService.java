package com.github.krystianmuchla.home.db.changelog;

import com.github.krystianmuchla.home.db.Sql;
import com.github.krystianmuchla.home.db.Transaction;
import com.github.krystianmuchla.home.exception.InternalException;
import com.github.krystianmuchla.home.util.InstantFactory;
import com.github.krystianmuchla.home.util.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        while (true) {
            final List<String> statements;
            try (final var stream = Resource.inputStream("db/changelog/" + changeId + ".sql")) {
                if (stream == null) {
                    break;
                }
                statements = readStatements(stream);
            } catch (final IOException exception) {
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

    private static List<String> readStatements(final InputStream stream) {
        final var result = new ArrayList<String>();
        try (final var scanner = new Scanner(stream)) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                result.add(scanner.next());
            }
        }
        return result;
    }
}
