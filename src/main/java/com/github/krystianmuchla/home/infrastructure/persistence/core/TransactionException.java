package com.github.krystianmuchla.home.infrastructure.persistence.core;

public class TransactionException extends RuntimeException {
    public TransactionException(Throwable cause) {
        super(cause);
    }
}
