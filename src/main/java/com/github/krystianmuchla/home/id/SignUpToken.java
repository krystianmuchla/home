package com.github.krystianmuchla.home.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class SignUpToken {
    private static final Logger LOG = LoggerFactory.getLogger(SignUpToken.class);
    public static final SignUpToken INSTANCE = new SignUpToken();

    private final Semaphore semaphore = new Semaphore(1);
    private String token;
    private Thread tokenExpirationThread;

    public boolean generateAndLog() {
        final var success = semaphore.tryAcquire();
        if (success) {
            token = token();
            tokenExpirationThread = tokenExpirationThread();
            tokenExpirationThread.start();
            LOG.info("Sign up token: " + token);
        }
        return success;
    }

    public boolean test(final String token) {
        if (semaphore.availablePermits() > 0) {
            return false;
        }
        final var success = Objects.equals(this.token, token);
        if (success) {
            LOG.info("Sign up token " + token + " has been consumed");
            tokenExpirationThread.interrupt();
        }
        return success;
    }

    private String token() {
        final var token = SecureRandomFactory.createIntegers(6, 10);
        final var builder = new StringBuilder(6);
        for (final var number : token) {
            builder.append(number);
        }
        return builder.toString();
    }

    private Thread tokenExpirationThread() {
        return new Thread(() -> {
            try {
                Thread.sleep(Duration.ofMinutes(1));
            } catch (final InterruptedException ignored) {
            }
            semaphore.release();
            LOG.info("Sign up token " + token + " has expired");
        });
    }
}
