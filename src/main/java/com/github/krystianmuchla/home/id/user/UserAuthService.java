package com.github.krystianmuchla.home.id.user;

import com.github.krystianmuchla.home.error.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserAuthService {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthService.class);
    private static final int MAX_FAILURES = 3;
    private static final Map<UUID, Integer> FAILURES = new ConcurrentHashMap<>();

    public static void validateAuth(final UUID userId) {
        final var failures = FAILURES.get(userId);
        if (failures == null) {
            return;
        }
        if (failures >= MAX_FAILURES) {
            throw new AuthenticationException();
        }
    }

    public synchronized static void incrementAuthFailures(final UUID userId) {
        final var failures = FAILURES.get(userId);
        if (failures == null) {
            FAILURES.put(userId, 1);
            authFailuresRemovalThread(userId).start();
        } else {
            FAILURES.put(userId, failures + 1);
        }
    }

    private synchronized static void decrementAuthFailures(final UUID userId) {
        final var failures = FAILURES.get(userId);
        if (failures == null) {
            return;
        }
        if (failures <= 1) {
            FAILURES.remove(userId);
        } else {
            FAILURES.put(userId, failures - 1);
        }
    }

    private static Thread authFailuresRemovalThread(final UUID userId) {
        return new Thread(() -> {
            do {
                try {
                    Thread.sleep(Duration.ofMinutes(5));
                } catch (final InterruptedException exception) {
                    LOG.warn("{}", exception.getMessage(), exception);
                }
                decrementAuthFailures(userId);
            } while (FAILURES.get(userId) != null);
        });
    }
}
