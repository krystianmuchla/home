package com.github.krystianmuchla.home.domain.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Semaphore;

public class SignUpToken {
    private static final Logger LOG = LoggerFactory.getLogger(SignUpToken.class);
    public static final SignUpToken INSTANCE = new SignUpToken();

    private final Semaphore availableAttempts = new Semaphore(1);
    private String token;
    private Thread tokenExpirationThread;

    public boolean generateAndLog() {
        var success = availableAttempts.tryAcquire();
        if (success) {
            token = token();
            tokenExpirationThread = startTokenExpirationThread();
            LOG.info("Sign up token: {}", token);
        }
        return success;
    }

    public boolean test(String token) {
        if (availableAttempts.availablePermits() > 0) {
            return false;
        }
        var success = Objects.equals(this.token, token);
        if (success) {
            LOG.info("Sign up token {} has been consumed", token);
        }
        tokenExpirationThread.interrupt();
        return success;
    }

    private String token() {
        var token = SecureRandomFactory.createIntegers(6, 10);
        var builder = new StringBuilder(6);
        for (var number : token) {
            builder.append(number);
        }
        return builder.toString();
    }

    private Thread startTokenExpirationThread() {
        return Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(Duration.ofMinutes(1));
            } catch (InterruptedException ignored) {
            }
            availableAttempts.release();
            LOG.info("Sign up token {} has expired", token);
        });
    }
}
