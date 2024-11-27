package com.github.krystianmuchla.home.infrastructure.persistence.core.changelog;

import com.github.krystianmuchla.home.application.exception.InternalException;
import com.github.krystianmuchla.home.application.util.InstantFactory;
import com.github.krystianmuchla.home.application.util.Resource;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Persistence;
import com.github.krystianmuchla.home.infrastructure.persistence.core.Transaction;
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
        if (!ChangelogPersistence.exists()) {
            Transaction.run(ChangelogPersistence::create);
        }
        var lastChange = ChangelogPersistence.getLastChange();
        int changeId;
        if (lastChange == null) {
            changeId = 1;
        } else {
            changeId = lastChange.id() + 1;
        }
        while (true) {
            List<String> statements;
            try (var stream = Resource.inputStream("db/changelog/" + changeId + ".sql")) {
                if (stream == null) {
                    break;
                }
                statements = readStatements(stream);
            } catch (IOException exception) {
                throw new InternalException(exception);
            }
            if (statements.getLast().isBlank()) {
                statements.removeLast();
            }
            int id = changeId;
            Transaction.run(() -> {
                statements.forEach(statement -> Persistence.executeUpdate(statement.trim()));
                ChangelogPersistence.createChange(new Change(id, InstantFactory.create()));
            });
            LOG.info("Executed database change with id: {}", changeId);
            changeId++;
        }
    }

    private static List<String> readStatements(InputStream stream) {
        var result = new ArrayList<String>();
        try (var scanner = new Scanner(stream)) {
            scanner.useDelimiter(";");
            while (scanner.hasNext()) {
                result.add(scanner.next());
            }
        }
        return result;
    }
}
