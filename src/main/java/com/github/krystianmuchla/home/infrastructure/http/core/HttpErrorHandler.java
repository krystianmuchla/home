package com.github.krystianmuchla.home.infrastructure.http.core;

import com.github.krystianmuchla.home.infrastructure.http.core.error.HttpException;
import com.github.krystianmuchla.home.infrastructure.persistence.core.TransactionException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class HttpErrorHandler {
    public static void handle(HttpExchange exchange, Exception exception) throws IOException {
        handle(exchange, (Throwable) exception);
    }

    private static void handle(HttpExchange exchange, Throwable throwable) throws IOException {
        switch (throwable) {
            case HttpException httpException -> httpException.handle(exchange);
            case TransactionException transactionException -> {
                var cause = transactionException.getCause();
                if (cause == null) {
                    new ResponseWriter(exchange).status(500).write();
                } else {
                    handle(exchange, cause);
                }
            }
            default -> new ResponseWriter(exchange).status(500).write();
        }
    }
}
