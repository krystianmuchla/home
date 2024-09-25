package com.github.krystianmuchla.home.infrastructure.persistence;

public class TransactionException extends RuntimeException {
    public TransactionException(Throwable cause) {
        super(cause);
    }
}
