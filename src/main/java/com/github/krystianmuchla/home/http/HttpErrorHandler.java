package com.github.krystianmuchla.home.http;

import com.github.krystianmuchla.home.exception.TransactionException;
import com.github.krystianmuchla.home.exception.http.HttpException;
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
                    ResponseWriter.write(exchange, 500);
                } else {
                    handle(exchange, cause);
                }
            }
            default -> ResponseWriter.write(exchange, 500);
        }
    }
}
