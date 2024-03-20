package com.github.krystianmuchla.home.id;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@Slf4j
public class SignUpToken {
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
            log.info("Sign up token: " + token);
        }
        return success;
    }

    public boolean test(final String token) {
        if (semaphore.availablePermits() > 0) {
            return false;
        }
        final var success = Objects.equals(this.token, token);
        if (success) {
            log.info("Sign up token " + token + " has been consumed");
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
            log.info("Sign up token " + token + " has expired");
        });
    }
}
